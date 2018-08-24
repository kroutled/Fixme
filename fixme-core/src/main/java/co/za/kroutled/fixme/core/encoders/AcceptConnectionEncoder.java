package co.za.kroutled.fixme.core.encoders;

import co.za.kroutled.fixme.core.messages.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.Charset;

public class AcceptConnectionEncoder extends MessageToByteEncoder<AcceptConnection> {
    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, AcceptConnection msg, ByteBuf outMsg)
    {
        if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString()))
        {
            outMsg.writeInt(msg.getTypeLength());
            outMsg.writeCharSequence(msg.getMessageType(), charset);
            if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION.toString()))
            {
                outMsg.writeInt(msg.getId());
                outMsg.writeInt(msg.getChecksumLength());
                outMsg.writeCharSequence(msg.getChecksum(), charset);
            }
        }
    }
}