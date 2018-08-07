package co.za.kroutled.fixme.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Broker {
    public static void main(String args[]) throws Exception
    {
        new Broker().go();
    }

    private void go() throws IOException, InterruptedException, ExecutionException
    {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5000);
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
            Thread.sleep(3000);
        }
        client.close();
    }
}
