package co.za.kroutled.fixme.core.encoders;

import co.za.kroutled.fixme.core.messages.BuyOrSell;
import co.za.kroutled.fixme.core.messages.MessageTypes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class BoSEncoder extends MessageToByteEncoder<BuyOrSell> {
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, BuyOrSell msg, ByteBuf outMsg)
    {
        outMsg.writeInt(msg.getTypeLength());
        outMsg.writeCharSequence(msg.getMessageType(), charset);
        if (msg.getMessageType().equals(MessageTypes.MESSAGE_BUY.toString()) ||
                msg.getMessageType().equals(MessageTypes.MESSAGE_SELL.toString()))
        {
            outMsg.writeInt(msg.getActionLength());
            outMsg.writeCharSequence(msg.getMessageAction(),charset);
            outMsg.writeInt(msg.getId());
            outMsg.writeInt(msg.getInstrumentLength());
            outMsg.writeCharSequence(msg.getInstrument(), charset);
            outMsg.writeInt(msg.getMarketId());
            outMsg.writeInt(msg.getPrice());
            outMsg.writeInt(msg.getQuantity());
            outMsg.writeInt(msg.getChecksumLength());
            outMsg.writeCharSequence(msg.getChecksum(), charset);
        }
    }
}
