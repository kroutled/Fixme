package co.za.kroutled.fixme.router;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Router implements Runnable {

    private int port;
    private String clientType;

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

                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast("handler", new ServerHandler());
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
            System.out.println(msg + " from " + ctx.channel().remoteAddress());
            ctx.writeAndFlush("Message has been received");
            newConnection(ctx);
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx)
        {
            System.out.println("Accepted a connection from " + ctx.channel().remoteAddress());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("Removed client from server " + ctx.channel().remoteAddress());
        }

        private void newConnection(ChannelHandlerContext ctx )//, Object msg)
        {
            String uniqueID = ctx.channel().remoteAddress().toString().substring(11);
            String tempMoB = clientType.equals("Broker") ? "0" : "1";
            uniqueID = uniqueID.concat(tempMoB);
            System.out.println("Assigned unique ID " + uniqueID + " to " + ctx.channel().remoteAddress());
            ctx.writeAndFlush("hello " + uniqueID);
        }
    }

}
