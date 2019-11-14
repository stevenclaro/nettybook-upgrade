/**
 * 
 */
package io.netty.cases.chapter.demo4.ays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.cases.chapter.demo4.HttpResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultPromise;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by ���ַ� on 2018/8/11.
 *
 */
public class HttpClientHandlerSys extends SimpleChannelInboundHandler<FullHttpResponse> {

	//DefaultPromise<HttpResponse> respPromise;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws UnsupportedEncodingException {
		ByteBuf body = Unpooled.wrappedBuffer("Http message!".getBytes("UTF-8"));
		DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
				"http://127.0.0.1/user?id=10&addr=NanJing", body);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
		//ctx.writeAndFlush(request);
		ctx.writeAndFlush(request).addListener(
				new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess())
						{
							//HttpResponse response = future.get();
							// System.out.print("The client received is :" +future.get().toString() );
							Object response = future.get();//get����ͬ�������ȡ��Ϣ�ķ����������첽�Ļ�ȡ����Ĵ�����

							io.netty.cases.chapter.demo4.HttpResponse res=(io.netty.cases.chapter.demo4.HttpResponse)response;
							if (res != null)
								System.out.print("The client received http response, the body is :" + new String(res.body()));
							//System.out.print("The client received is :" +future.channel().toString() );
						}
						else
						{
							System.out.print("Ϊʲô��������ʧ�ܷ�֧�أ� :" + new Date());
						}
					}
				});
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			FullHttpResponse msg) throws Exception {
		if (msg.decoderResult().isFailure())
			throw new Exception("Decode HttpResponse error : " + msg.decoderResult().cause());
		//ʵ������ʱ�򣬰�msg����ȥ��Ȼ������Ĺ��캯���У����и���һ���µġ�
		io.netty.cases.chapter.demo4.HttpResponse response = new HttpResponse(msg);
		//ִ��֮��FullHttpResponse msg���ͷ�
		//respPromise.setSuccess(response);

		System.out.print("The client received http response, the body is :" +new String(response.body())+"sjk"+  new Date());

	}
	
	 @Override
	    public void exceptionCaught(
             ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close();
	    }


	 
}
