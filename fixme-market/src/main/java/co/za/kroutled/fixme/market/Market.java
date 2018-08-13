package co.za.kroutled.fixme.market;

import co.za.kroutled.fixme.core.Client;

public class Market extends Client {
    public static void main(String args[]) throws Exception
    {
        new Market(5001);
    }

    Market(int port) throws Exception
    {
        super(port);
    }
}
