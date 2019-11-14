package io.netty.cases.chapter.demo4.ays;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * Created by 李林峰 on 2018/8/11.
 * 采用异步方式进行调用
 */
public class HttpClientAys {

	private Channel channel;
    HttpClientHandlerSys handler = new HttpClientHandlerSys();
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
	


    public static void main(String[] args) throws Exception {
    	HttpClientAys client = new HttpClientAys();
        client.connect("127.0.0.1", 18084);

    }
}
