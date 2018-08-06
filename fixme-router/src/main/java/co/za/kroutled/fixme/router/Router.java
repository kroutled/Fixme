package co.za.kroutled.fixme.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Router {
    public static void main(String[] args) throws Exception {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        String host = "localhost";
        int port = 5000;
        InetSocketAddress socketAddr = new InetSocketAddress(host, port); //creates a socket(ip + port).
        server.bind(socketAddr); //binds the open asynchronousserversocketchannel to the local address and specified port number.
        System.out.format("Server is listening at %s%n", socketAddr);
        Future<AsynchronousSocketChannel> acceptFuture = server.accept(); //allows accepting of connections to the socket from clients etc
        AsynchronousSocketChannel worker = acceptFuture.get(); //s10, TimeUnit.SECONDS) - if we want a timeout on waiting for a query response from the acceptFuture object;
    }

    public void runServer()
    {

    }
}

