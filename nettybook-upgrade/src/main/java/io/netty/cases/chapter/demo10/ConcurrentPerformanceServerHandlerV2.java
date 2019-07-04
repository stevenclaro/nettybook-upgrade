/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.cases.chapter.demo10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 李林峰 on 2018/8/19.
 */
@ChannelHandler.Sharable
public class ConcurrentPerformanceServerHandlerV2 extends ChannelInboundHandlerAdapter {
    static AtomicInteger counter = new AtomicInteger(0);
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    static ExecutorService executorService = Executors.newFixedThreadPool(100);
//我感觉数据库的连接池可以放在这个地方，进行获取

    public ConcurrentPerformanceServerHandlerV2()
    {
       /* scheduledExecutorService.scheduleAtFixedRate(()->
        {
            //int qps = counter.getAndSet(0);
            int qps = 0;
            System.out.println("The server QPS is : " + qps);
        },0,1000, TimeUnit.MILLISECONDS);*/
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ((ByteBuf)msg).release();
        executorService.execute(()->
        {
            //下面的注释说明了msg如果不被release，是可以被传入到execute中的
        //    System.out.println("The server msg is : " +msg.toString());

           /* counter.incrementAndGet();
            System.out.println("The server channelRead QPS is : " +counter.incrementAndGet());
*/

            //counter.getAndSet(0);
            System.out.println("The server QPS is : " + counter.incrementAndGet());

            //业务逻辑处理，模拟业务访问DB、缓存等，时延从100-1000毫秒之间不等
            Random random = new Random();
            try
            {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        });
      //  counter.getAndSet(0);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == SslHandshakeCompletionEvent.SUCCESS) {
            //执行流控逻辑
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
