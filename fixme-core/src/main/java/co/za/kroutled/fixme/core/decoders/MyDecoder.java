package co.za.kroutled.fixme.core.decoders;

import co.za.kroutled.fixme.core.messages.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class MyDecoder extends ReplayingDecoder {
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outMsg) throws Exception
    {
        Fix msg = new Fix();
        msg.setMessageType(in.readCharSequence(in.readInt(), charset).toString());
        if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString()))
        {

            AcceptConnection con = new AcceptConnection();
            con.setMessageType(msg.getMessageType());
            con.setId(in.readInt());
            con.setChecksum(in.readCharSequence(in.readInt(), charset).toString());
            outMsg.add(con);

        }
        else if (msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
                msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString()))
        {
            BuyOrSell ret = new BuyOrSell();
            ret.setMessageType(msg.getMessageType());
            ret.setMessageAction(in.readCharSequence(in.readInt(), charset).toString());
            ret.setId(in.readInt());
            ret.setInstrument(in.readCharSequence(in.readInt(), charset).toString());
            ret.setMarketId(in.readInt());
            ret.setQuantity(in.readInt());
            ret.setPrice(in.readInt());
            ret.setNewChecksum();
            System.out.println(ret);
            outMsg.add(ret);
        }
    }
}
