package co.za.kroutled.fixme.router;

import co.za.kroutled.fixme.core.*;
import co.za.kroutled.fixme.core.decoders.MyDecoder;
import co.za.kroutled.fixme.core.encoders.AcceptConnectionEncoder;
import co.za.kroutled.fixme.core.encoders.BoSEncoder;
import co.za.kroutled.fixme.core.messages.AcceptConnection;
import co.za.kroutled.fixme.core.messages.BuyOrSell;
import co.za.kroutled.fixme.core.messages.Fix;
import co.za.kroutled.fixme.core.messages.MessageTypes;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Router implements Runnable {

    private int port;
    private String clientType;
    private static HashMap<Integer, ChannelHandlerContext> routingTable = new HashMap<>();

    public static void main(String[] args) throws Exception
    {
        Router brokerServer = new Router(5000);
        Thread brokerServerThread = new Thread(brokerServer);
        brokerServerThread.start();

        Router marketServer = new Router(5001);
        Thread marketServerThread = new Thread(marketServer);
        marketServerThread.start();
    }

    public Router(int port)
    {
        this.port = port;
        this.clientType = port == 5000 ? "Broker" : "Market";
    }

    //listens for incoming connections and then hands them off for processing
    @Override
    public void run()
    {
        //accepts new connections as they arrive and pass them to worker for proccessing
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //determines how to server will proccess incoming connections
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel (SocketChannel ch) throws Exception
                        {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new MyDecoder());
                            pipeline.addLast(new AcceptConnectionEncoder());
//                            pipeline.addLast(new BoSEncoder());
                            pipeline.addLast(new ServerHandler());
                        }
            }).option(ChannelOption.SO_REUSEADDR, true);

            //bind server to port and start listening for incoming connections
            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("Server has started \nWaiting for connections....");
            f.channel().closeFuture().sync();
            //bootstrap.bind(port).sync().channel().closeFuture().sync();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    class ServerHandler extends ChannelInboundHandlerAdapter
    {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            Fix message = (Fix)msg;
            System.out.println(message);

            if (message.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString())) {

                newConnection(ctx, msg);

            }
            else if (message.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
                    message.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString()))
            {
                BuyOrSell BoS = (BuyOrSell)msg;
                try
                {
                    channelFromTable(BoS.getMarketId()).channel().writeAndFlush(BoS);
                    System.out.println("Making request to Market: " + BoS.getMarketId());
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                    BoS.setMessageAction(MessageTypes.MESSAGE_REJECT.toString());
                    BoS.setNewChecksum();
                    ctx.writeAndFlush(BoS);
                }
            }
            //showTable();
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("Removed client from server " + ctx.channel().remoteAddress());
            removeID(ctx);
        }

        private void newConnection(ChannelHandlerContext ctx, Object msg)
        {
            System.out.println("Test");
            AcceptConnection con = (AcceptConnection)msg;
            String uniqueID = ctx.channel().remoteAddress().toString().substring(11);
            String tempMoB = clientType.equals("Broker") ? "0" : "1";
            uniqueID = uniqueID.concat(tempMoB);
            con.setId(Integer.valueOf(uniqueID));
            con.setNewChecksum();
            //ctx.writeAndFlush(ctx);
            ctx.writeAndFlush(con);
            routingTable.put(Integer.valueOf(uniqueID), ctx);
            System.out.println("Assigned unique ID " + uniqueID + " to " + ctx.channel().remoteAddress());
        }
    }

    private void removeID(ChannelHandlerContext ctx)
    {
        for (HashMap.Entry<Integer, ChannelHandlerContext> entry : routingTable.entrySet()) {
            if (entry.getValue() == ctx)
                routingTable.remove(entry.getKey());
        }
    }

//    private void showTable() throws IOException {
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        String input = in.readLine();
//        if (input.equals("table"))
//        {
//            if (routingTable != null)
//            {
//                for (HashMap.Entry<Integer, ChannelHandlerContext> entry : routingTable.entrySet())
//                    System.out.println("[" + entry.getKey() + "] - " + isMarketOrBroker(entry.getKey().toString()));
//            }
//        }
//    }

    private String isMarketOrBroker(String id)
    {
        if (id.substring(5).equals("0"))
            return "Broker";
        else
            return "Market";

    }

    private ChannelHandlerContext channelFromTable(int id)
    {
        return routingTable.get(id);
    }

}
