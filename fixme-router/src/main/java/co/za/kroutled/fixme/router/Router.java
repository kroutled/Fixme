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
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg + " from " + ctx.channel().remoteAddress());
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception
        {
            System.out.println("Accepted a connection from " + ctx.channel().remoteAddress());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Removed client from server " + ctx.channel().remoteAddress());
        }
    }

}





















//    private int port;
//    private String clientType;
//    //private HashMap<Integer, AsynchronousServerSocketChannel> routingtable = new HashMap<>();
//
//
//    public Router(int port)
//    {
//        this.port = port;
//        switch (port) {
//            case 5000:
//                clientType = "Broker";
//                break;
//            case 5001:
//                clientType = "Market";
//                break;
//
//        }
//    }
//    @Override
//    public void run()
//    {
//
//
//    }
//        try {
//            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
//            InetSocketAddress hostAddress = new InetSocketAddress("localhost", this.port);
//            serverChannel.bind(hostAddress);
//
//        System.out.println("Server channel bound to port: " + hostAddress.getPort());
//        System.out.println("Waiting for " + clientType + " client to connect...");
//
//        Future acceptResult = serverChannel.accept();
//        AsynchronousSocketChannel clientChannel = (AsynchronousSocketChannel) acceptResult.get();
//
//        System.out.println("Messages from " + clientType +": " + clientChannel.getRemoteAddress());
//
//        if ((clientChannel != null) && (clientChannel.isOpen()))
//        {
//            while (true)
//            {
//                ByteBuffer buffer = ByteBuffer.allocate(32);
//                Future result = clientChannel.read(buffer);
//
//                while (!result.isDone())
//                {
//                    //do nothing
//                }
//
//                buffer.flip();
//                String message = new String(buffer.array()).trim();
//                System.out.println(message);
//                if (message.equals("Bye"))
//                {
//                    break;
//                }
//
//                buffer.clear();
//            }
//            clientChannel.close();
//        }
//        serverChannel.close();
//        }catch (IOException e)
//        { e.printStackTrace();}
//        catch (InterruptedException e)
//        { e.printStackTrace(); }
//        catch (ExecutionException e)
//        { e.printStackTrace(); }
//    }
//}

