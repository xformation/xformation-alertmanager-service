/*
 * */
package com.synectiks.process.server.inputs.syslog.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;

public class SyslogTCPFramingRouterHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final int maxFrameLength;
    private final ByteBuf[] delimiter;
    private ChannelInboundHandler handler = null;

    public SyslogTCPFramingRouterHandler(int maxFrameLength, ByteBuf[] delimiter) {
        this.maxFrameLength = maxFrameLength;
        this.delimiter = delimiter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg.isReadable()) {
            // "Dynamically manipulating a pipeline is relatively an expensive operation."
            // https://stackoverflow.com/a/28846565
            if (handler == null) {
                if (usesOctetCountFraming(msg)) {
                    handler = new SyslogOctetCountFrameDecoder();
                } else {
                    handler = new DelimiterBasedFrameDecoder(maxFrameLength, delimiter);
                }
            }

            handler.channelRead(ctx, ReferenceCountUtil.retain(msg));
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean usesOctetCountFraming(ByteBuf message) {
        // Octet counting framing needs to start with a non-zero digit.
        // See: http://tools.ietf.org/html/rfc6587#section-3.4.1
        final byte firstByte = message.getByte(0);
        return '0' < firstByte && firstByte <= '9';
    }
}
