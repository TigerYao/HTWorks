package com.huatu.handheld_huatu.network.tcp;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.arena.AthleticsRequest;
import com.huatu.handheld_huatu.mvpmodel.arena.AthleticsResponse;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.HashMap;

/*import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;*/

public class NettyClient {
   /* private static final String tag = NettyClient.class.getSimpleName();
    private Gson mGson = new Gson();
    private static final String TAG = "NettyClient";
    private static NettyClient sNettyClient;
//    private static String host = "ns.huatu.com";//"";
    private static String host = "123.103.86.52";//"ns.huatu.com";
//    private static String host = "123.103.86.58";//"ns.huatu.com";
    private static int port = 14444;
    private HashMap<Integer, String> mMarks = new HashMap<>();
    private Bootstrap mBootstrap;
    public static NettyClient getInstance() {
        if (sNettyClient == null) {
            synchronized (NettyClient.class) {
                if (sNettyClient == null) {
                    sNettyClient = new NettyClient();
                }
            }
        }
        return sNettyClient;
    }

    private NettyClient() {
        //不同环境下设置测试按钮是否可见
        if (BuildConfig.isDebug) {
            host = "123.103.86.52";
            String url=SpUtils.getHostUrl();
            if (!TextUtils.isEmpty(url)) {
                if(url.contains("ns.huatu.com")) {
                    host = "ns.huatu.com";//"";
                }
            }
        } else {
            host = "ns.huatu.com";//"";
        }
    }

    public void addMark(int order, String ticket) {
        mMarks.put(order, ticket);
    }

    public boolean hasMark(String ticket) {
        return mMarks.containsValue(ticket);
    }

    private class AppClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Log.i("AppClientHandler", "channelActive: 建立连接" + ctx.name());
            ConnectionKeeper.add(ctx);
            writeMessage(10001, 0);
        }

        //这里是断线要进行的操作
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(String.format("%s. channelInactive: %s", tag, ConnectionKeeper.getChannelId(ctx)));
            ConnectionKeeper.close(ctx);
        }

        //这里是出现异常的话要进行的操作
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(String.format("%s. exceptionCaught: %s", tag, ConnectionKeeper.getChannelId(ctx)));
            cause.printStackTrace();
            ConnectionKeeper.close(ctx);
            Intent intent = new Intent();
            intent.setAction("com.athletic.error");
            UniApplicationContext.getContext().sendBroadcast(intent);
        }

        //这里是接受服务端发送过来的消息

        @Override
        protected void messageReceived(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            Log.i("TCP Received", s);
            AthleticsResponse response = mGson.fromJson(s, AthleticsResponse.class);
            if (!TextUtils.isEmpty(response.ticket) && !hasMark(response.ticket)) {
                return;
            }
            Intent intent = new Intent();
            switch (response.code) {
                case 100003:
                    //身份验证失败
                    intent.setAction("com.athletic.not.pass");
                    break;
                case 50000:
                    //身份认证成功
                    intent.setAction("com.athletic.pass");
                    break;
                case 50001:
                    //加入游戏成功
                    intent.setAction("com.athletic.join");
                    break;
                case 50002:
                    //成功离开游戏
                    intent.setAction("com.athletic.exit");
                    break;
                case 50003:
                    //存在未完成的竞技场
                    intent.setAction("com.athletic.continue");
                    break;
                case 50004:
                    //用户不存在未完成的竞技场
                    intent.setAction("com.athletic.un.exist");
                    break;
                case 50005:
                    //新玩家加入房间通知
                    intent.setAction("com.athletic.new");
                    break;
                case 50006:
                    //开始游戏通知
                    intent.setAction("com.athletic.start");
                    break;
                case 50007:
                    //用户离开房间
                    intent.setAction("com.athletic.leave");
                    break;
                case 50008:
                    //用户做题进度通知
                    intent.setAction("com.athletic.progress");
                    break;
                case 50009:
                    //通知用户查看竞技结果
                    intent.setAction("com.athletic.result");
                    break;
            }
            intent.putExtra("player", s);
            UniApplicationContext.getContext().sendBroadcast(intent);
        }
    }

    private final Bootstrap b = new Bootstrap(); // (1)
    private EventLoopGroup workerGroup;

    private void send(final ChannelHandlerContext ctx, final int action, int moduleId) {
        final Channel c = ctx.channel();
        final AthleticsRequest request = new AthleticsRequest();
        final String ticket = System.currentTimeMillis() + "";
        request.params.token = SpUtils.getToken();
        request.action = action;
        request.ticket = ticket;
        if (moduleId != 0) {
            request.params.moduleId = moduleId;
        }

        c.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Log.i("operationComplete", "operationComplete: ticket" + ticket);
                    addMark(action, ticket);
                } else {
                    System.err.println(String.format("Send Error. cause=%s", future.cause()));
                    future.cause().printStackTrace();
                    ConnectionKeeper.close(ctx);
                }

            }
        });
    }

    public void writeMessage(int action, int moduleId) {
        ChannelHandlerContext ctx = ConnectionKeeper.get();
        if (null == ctx) {
            System.out.println(String.format("%s. has no context. writeMessage failed. action=%s", tag, action));
            return;
        }
        send(ctx, action, moduleId);
    }

    public void start() {
        ChannelHandlerContext ctx = ConnectionKeeper.get();
        if (ctx != null) {
            if (ctx.channel().isActive()) {
                System.out.println("已经连接");
            } else {
                connect();
            }
        } else {
            connect();
        }
    }

    public void connect() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap();
        final LineBaseFrameEncoder lineBaseFrameEncoder = new LineBaseFrameEncoder();
        final RequestEncoder requestEncoder = new RequestEncoder();
        final RequestDecoder requestDecoder = new RequestDecoder();
        mBootstrap.channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .group(eventLoopGroup)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //以行分隔符
                        //数据转换成字符串
                        socketChannel.pipeline().addLast(new StringEncoder());
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024 * 1024));
                        socketChannel.pipeline().addLast(lineBaseFrameEncoder);
                        socketChannel.pipeline().addLast(requestEncoder);
                        //定时发送心跳检测
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 180));
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new HeartbeatHandler());
                        socketChannel.pipeline().addLast(requestDecoder);
                        socketChannel.pipeline().addLast(new AppClientHandler());
                    }
                });

        ChannelFuture future = null;
        try {
            Log.d(TAG, "Netty host! "+host );
            Log.d(TAG, "Netty port! "+port );
            future = mBootstrap.connect(host, port).sync();
            future.awaitUninterruptibly();
            if (future.isCancelled()) {
                Log.e(TAG, "Netty Close by User !!!!!");
            } else if (!future.isSuccess()) {
                Log.e(TAG, "Netty Error !!!!!");
                future.cause().printStackTrace();
            } else {
                Log.d(TAG, "Netty Connection Success !!");
            }
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Netty e! "+e.getMessage() );
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void close() {
        if (null != workerGroup) {
            workerGroup.shutdownGracefully();
        }
        workerGroup = null;
    }*/
}
