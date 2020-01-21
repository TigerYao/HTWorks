package com.huatu.handheld_huatu.network.tcp;
/*
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;*/

/*
public class HeartbeatHandler extends SimpleChannelInboundHandler<String> {
    private static final String PING = "PING";//发送心跳内容
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer(PING + "\r\n", CharsetUtil.UTF_8));  //2
    private static final String PONG = "PONG";//心跳响应

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //发送的心跳并添加一个侦听器，如果发送操作失败将关闭连接
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);  //3
        } else {//事件不是一个 IdleStateEvent 的话，就将它传递给下一个处理程序
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.equals(PING)) {//对方要求发送心跳
            ctx.writeAndFlush(PONG);
        } else {//传递到下一个handler
            ctx.fireChannelRead(msg);
        }
    }
}
*/
