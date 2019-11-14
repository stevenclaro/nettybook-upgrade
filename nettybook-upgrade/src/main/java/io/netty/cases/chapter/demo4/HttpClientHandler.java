/**
 * 
 */
package io.netty.cases.chapter.demo4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.DefaultPromise;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 李林峰 on 2018/8/11.
 *
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	DefaultPromise<HttpResponse> respPromise;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			FullHttpResponse msg) throws Exception {
		if (msg.decoderResult().isFailure())
			throw new Exception("Decode HttpResponse error : " + msg.decoderResult().cause());
		//实例化的时候，把msg传过去，然后在类的构造函数中，进行复制一个新的。
		HttpResponse response = new HttpResponse(msg);
		//执行之后，FullHttpResponse msg被释放
		respPromise.setSuccess(response);//在这个方法中，直接就通知了listener。另外的线程就启动了。所以执行的顺序上可能在本方法中下一句执行或者在client端获得同步的调用结果
		//所以2个地方输出的日志有可能一会儿在前，一会儿在后。
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		System.out.print("The client received http response, the body is :" + new String(response.body())+ df.format(new Date())+"HttpClientHandler"+"\r\n");

	}
	
	 @Override
	    public void exceptionCaught(
             ChannelHandlerContext ctx, Throwable cause) throws Exception {
	        cause.printStackTrace();
	        ctx.close();
	    }

	public DefaultPromise<HttpResponse> getRespPromise() {
		return respPromise;
	}

	public void setRespPromise(DefaultPromise<HttpResponse> respPromise) {
		this.respPromise = respPromise;
	}
	 
}
