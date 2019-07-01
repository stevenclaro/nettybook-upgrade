package io.netty.cases.chapter.demo4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ExecutionException;

/**
 * Created by 李林峰 on 2018/8/11.
 * 采用异步方式进行调用
 */
public class HttpClientAys {

	private Channel channel;
    HttpClientHandler handler = new HttpClientHandler();
	private void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpClientCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(Short.MAX_VALUE));
                    ch.pipeline().addLast(handler);
                }
            });            
            ChannelFuture f = b.connect(host, port).sync();
            //绑定之后，就会产生ChannelFuture
            channel = f.channel();
        
    }
	
	private HttpResponse blockSend(FullHttpRequest request) throws InterruptedException, ExecutionException
	{
          request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
          //获取channel的默认返回Promise
          DefaultPromise<HttpResponse> respPromise = new DefaultPromise<HttpResponse>(channel.eventLoop());
          handler.setRespPromise(respPromise);
        //  channel.writeAndFlush(request);
          //采用异步的调用方式
        //但是问题是，如何获取处理Future的结果？
        channel.writeAndFlush(request).addListener(
        new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess())
                {
                    //HttpResponse response = future.get();
                }

            }
        });

          HttpResponse response = respPromise.get();
          if (response != null)
        	  System.out.print("The client received http response, the body is :" + new String(response.body()));
          return response;
	}

    public static void main(String[] args) throws Exception {
    	HttpClientAys client = new HttpClientAys();
        client.connect("127.0.0.1", 18084);
        ByteBuf body = Unpooled.wrappedBuffer("Http message!".getBytes("UTF-8"));
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                "http://127.0.0.1/user?id=10&addr=NanJing", body);
        HttpResponse response = client.blockSend(request);
    }
}
