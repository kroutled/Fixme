package co.za.kroutled.fixme.broker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import co.za.kroutled.fixme.core.Client;


public class Broker extends Client {
    public static void main(String args[]) throws Exception
    {
        new Broker(5000).run();
    }

    Broker(int port) throws Exception
    {
        super(port, "localhost");
    }
}
