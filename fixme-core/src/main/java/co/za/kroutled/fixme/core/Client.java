package co.za.kroutled.fixme.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {

    private int port;
    private String clientType;

    public Client(int port) throws Exception
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
        go();
    }

    private void go() throws IOException, InterruptedException, ExecutionException
    {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", this.port);
        Future future = client.connect(hostAddress);
        future.get();

        System.out.println("Client started: " + client.isOpen());
        System.out.println("Sending messages to server: ");

        String[] messages = new String[] {"What the poes!?", "Hello?...HELLOW!!", "Server can you hear me?", "Bye"};
        for (int i = 0; i < messages.length; i++)
        {
            byte [] message = new String(messages[i]).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            Future result = client.write(buffer);

            while (!result.isDone())
            {
                System.out.println("...");
            }

            System.out.println(messages[i]);
            buffer.clear();
            Thread.sleep(2000);
        }
        client.close();
    }
}
