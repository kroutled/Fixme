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
    protected void decode(ChannelHandlerContext ctx, ByteBuf inMsg, List<Object> outMsg) throws Exception
    {
        Fix msg = new Fix();
        msg.setMessageType(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
        if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString()))
        {

            AcceptConnection con = new AcceptConnection();
            con.setMessageType(msg.getMessageType());
            con.setId(inMsg.readInt());
            con.setChecksum(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            outMsg.add(con);

        }
        else if (msg.getMessageType().equals(MessageTypes.MESSAGE_BUY) ||
                msg.getMessageType().equals(MessageTypes.MESSAGE_SELL))
        {
            BuyOrSell BoS = new BuyOrSell();
            BoS.setMessageType(msg.getMessageType());
            BoS.setMessageAction(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            BoS.setId(inMsg.readInt());
            BoS.setInstrument(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            BoS.setMarketId(inMsg.readInt());
            BoS.setQuantity(inMsg.readInt());
            BoS.setPrice(inMsg.readInt());
            BoS.setNewChecksum();
            outMsg.add(BoS);
        }
    }
}
