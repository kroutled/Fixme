package co.za.kroutled.fixme.core;

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

    private int port;
    private String host;
    private String clientType;

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
        System.out.println("Client");
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

                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast("handler", new clientHandler());
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

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(clientType + " is connecting to router..");
            ctx.writeAndFlush("New client added");
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
                ctx.writeAndFlush(userInput);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
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