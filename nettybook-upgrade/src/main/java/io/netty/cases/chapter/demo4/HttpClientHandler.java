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
 * Created by ���ַ� on 2018/8/11.
 *
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	DefaultPromise<HttpResponse> respPromise;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			FullHttpResponse msg) throws Exception {
		if (msg.decoderResult().isFailure())
			throw new Exception("Decode HttpResponse error : " + msg.decoderResult().cause());
		//ʵ������ʱ�򣬰�msg����ȥ��Ȼ������Ĺ��캯���У����и���һ���µġ�
		HttpResponse response = new HttpResponse(msg);
		//ִ��֮��FullHttpResponse msg���ͷ�
		respPromise.setSuccess(response);//����������У�ֱ�Ӿ�֪ͨ��listener��������߳̾������ˡ�����ִ�е�˳���Ͽ����ڱ���������һ��ִ�л�����client�˻��ͬ���ĵ��ý��
		//����2���ط��������־�п���һ�����ǰ��һ����ں�
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
