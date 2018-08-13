package co.za.kroutled.fixme.router;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.net.InetSocketAddress;

public class Router implements Runnable {

    private int port;
    private String clientType;
    //private HashMap<Integer,>

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
    public void run()
    {
        try {
            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", this.port);
            serverChannel.bind(hostAddress);

        System.out.println("Server channel bound to port: " + hostAddress.getPort());
        System.out.println("Waiting for " + clientType + " client to connect...");

        Future acceptResult = serverChannel.accept();
        AsynchronousSocketChannel clientChannel = (AsynchronousSocketChannel) acceptResult.get();

        System.out.println("Messages from " + clientType +": " + clientChannel.getRemoteAddress());

        if ((clientChannel != null) && (clientChannel.isOpen()))
        {
            while (true)
            {
                ByteBuffer buffer = ByteBuffer.allocate(32);
                Future result = clientChannel.read(buffer);

                while (!result.isDone())
                {
                    //do nothing
                }

                buffer.flip();
                String message = new String(buffer.array()).trim();
                System.out.println(message);
                if (message.equals("Bye"))
                {
                    break;
                }

                buffer.clear();
            }
            clientChannel.close();
        }
        serverChannel.close();
        }catch (IOException e)
        { e.printStackTrace();}
        catch (InterruptedException e)
        { e.printStackTrace(); }
        catch (ExecutionException e)
        { e.printStackTrace(); }
    }
}

