package co.za.kroutled.fixme.core;

import co.za.kroutled.fixme.core.decoders.MyDecoder;
import co.za.kroutled.fixme.core.encoders.*;
import co.za.kroutled.fixme.core.messages.AcceptConnection;
import co.za.kroutled.fixme.core.messages.BuyOrSell;
import co.za.kroutled.fixme.core.messages.Fix;
import co.za.kroutled.fixme.core.messages.MessageTypes;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client implements Runnable{

    private int     port;
    private String  host;
    private String  clientType;
    private int     ID;

    public Client(int port, String host) throws Exception {
        this.port = port;
        this.host = host;
        switch (port) {
            case 5000:
                clientType = "Broker";
                break;
            case 5001:
                clientType = "Market";
                break;
        }
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //used to set up a channel
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel (SocketChannel ch) throws Exception
                        {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new MyDecoder());
                            pipeline.addLast(new AcceptConnectionEncoder());
                            pipeline.addLast(new BoSEncoder());
                            pipeline.addLast(new clientHandler());
                        }
                    }).option(ChannelOption.SO_REUSEADDR, true);

            ChannelFuture f = bootstrap.connect(host, port).sync();
            f.channel().closeFuture().sync();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        finally { workerGroup.shutdownGracefully(); }
    }

    //used to handle all decoded incoming strings from the server
    class clientHandler extends ChannelInboundHandlerAdapter
    {

        //as the channel connects successfully and becomes active
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(clientType + " is connecting to router..");
            AcceptConnection msg = new AcceptConnection(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString(), 0, 0);
            ctx.writeAndFlush(msg);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Fix message = (Fix)msg;
            if (message.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString()))
            {
                AcceptConnection conMsg = (AcceptConnection)msg;
                ID = conMsg.getId();
                System.out.println(clientType + ": " + ID + " has connected to the router");

            }
            else if (message.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
                    message.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString()))
            {
                BuyOrSell BoSMsg = (BuyOrSell)msg;
                if (checkForBrokerAnswerFromMarket(BoSMsg))
                    return;
                if (BoSMsg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString()))
                    sellToMarketLogic(ctx, BoSMsg);
                else
                    buyFromMarketLogic(ctx, BoSMsg);
            }
        }


        private boolean checkForBrokerAnswerFromMarket(BuyOrSell ret) {
            if (ret.getMessageAction().equals(MessageTypes.MESSAGE_EXECUTE.toString()) ||
                    ret.getMessageAction().equals(MessageTypes.MESSAGE_REJECT.toString())) {
                System.out.println("Answer for your request: " + ret.getMessageAction());
                return true;
            }
            return false;
        }

        private void    sellToMarketLogic(ChannelHandlerContext ctx, BuyOrSell BoS)
        {
            System.out.println(BoS.getQuantity());
            System.out.println(BoS.getPrice());
            if (BoS.getQuantity() > 10 || BoS.getQuantity() < 1000)
            {
                if (BoS.getPrice() > 5000)
                {
                    System.out.println("Reject. That's just too expensive.");
                    BoS.setMessageAction(MessageTypes.MESSAGE_REJECT.toString());
                }
                else
                {
                    System.out.println("EXECUTE. Pleasure doing business.");
                    BoS.setMessageAction(MessageTypes.MESSAGE_EXECUTE.toString());
                }
            }
            else
            {
                System.out.println("REJECT. You must be mad, that's not worth it!");
                BoS.setMessageAction(MessageTypes.MESSAGE_REJECT.toString());
            }
            BoS.setNewChecksum();
            ctx.writeAndFlush(BoS);
        }

        private void    buyFromMarketLogic(ChannelHandlerContext ctx, BuyOrSell BoS)
        {
            System.out.println("I ain't buying shit pal!");
            BoS.setMessageAction(MessageTypes.MESSAGE_REJECT.toString());
            BoS.setNewChecksum();
            ctx.writeAndFlush(BoS);
        }

        private String getUserInput() throws Exception
        {
            System.out.println("Enter request message of type: [sell || buy] [market id] [instrument] [quantity] [price]");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        }

        private void writeToChannel(ChannelHandlerContext ctx)
        {
            try
            {
                String userInput = getUserInput();
                if (userInput.length() == 0)
                    throw new Exception("Empty input");
                else if (clientType.equals("Broker"))
                    brokerInputHandler(ctx, userInput);
                ctx.writeAndFlush(userInput);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                writeToChannel(ctx);
            }
        }

        //splits up the message from user and splits it up before sending it to the router
        private void brokerInputHandler(ChannelHandlerContext ctx, String input)
        {
            String inputs[] = input.split(" ");
            try
            {
                if (inputs.length != 5)
                    throw new Exception();

                BuyOrSell outMsg = null;
                int     marketId = Integer.valueOf(inputs[1]);
                String  instrument = inputs[2];
                int     quantity = Integer.valueOf(inputs[3]);
                int     price = Integer.valueOf(inputs[4]);

                if (inputs[0].toLowerCase().equals("buy"))
                    outMsg = new BuyOrSell(MessageTypes.MESSAGE_BUY.toString(), marketId, "-", ID, instrument, quantity, price);
                else if (inputs[0].toLowerCase().equals("sell"))
                    outMsg = new BuyOrSell(MessageTypes.MESSAGE_SELL.toString(), marketId, "-", ID, instrument, quantity, price);
                outMsg.setNewChecksum();
                System.out.println("Sending message to router...");
                ctx.writeAndFlush(outMsg);
            }
            catch(Exception e)
            {
                System.out.println("Invalid input");
                writeToChannel(ctx);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx)
        {
            if (clientType.equals("Broker"))
                writeToChannel(ctx);
        }
    }
}