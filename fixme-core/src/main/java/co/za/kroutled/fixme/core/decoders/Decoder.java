package co.za.kroutled.fixme.core.decoders;

import co.za.kroutled.fixme.core.messages.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class Decoder extends ReplayingDecoder {
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inMsg, List<Object> outMsg) throws Exception
    {
        Fix msg = new Fix();
        msg.setMessageType(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
        if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION))
        {
            acceptConnection ret = new acceptConnection();
            ret.setMessageType(msg.getMessageType());
            ret.setId(inMsg.readInt());
            ret.setChecksum(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            outMsg.add(ret);

        }
        else if (msg.getMessageType().equals(MessageTypes.MESSAGE_BUY) ||
                msg.getMessageType().equals(MessageTypes.MESSAGE_SELL))
        {
            BuyOrSell ret = new BuyOrSell();
            ret.setMessageType(msg.getMessageType());
            ret.setMessageAction(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            ret.setId(inMsg.readInt());
            ret.setInstrament(inMsg.readCharSequence(inMsg.readInt(), charset).toString());
            ret.setMarketId(inMsg.readInt());
            ret.setQuantity(inMsg.readInt());
            ret.setPrice(inMsg.readInt());
            ret.setNewChecksum();
            outMsg.add(ret);
        }
    }
}
