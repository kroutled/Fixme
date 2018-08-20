package co.za.kroutled.fixme.core.encoders;

import co.za.kroutled.fixme.core.messages.MessageTypes;
import co.za.kroutled.fixme.core.messages.acceptConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.Charset;

//public class AcceptConnectionEncoder extends MessageToByteEncoder {
//    private final Charset charset = Charset.forName("UTF-8");
//
//    @Override
//    protected void encode(ChannelHandlerContext ctx, acceptConnection msg, ByteBuf outMsg) throws Exception
//    {
//        if (msg.getMessageType().equals(MessageTypes.MESSAGE_ACCEPT_CONNECTION))
//        {
//
//        }
//    }
//}