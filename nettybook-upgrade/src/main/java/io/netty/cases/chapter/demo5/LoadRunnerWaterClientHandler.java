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
package io.netty.cases.chapter.demo5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Created by 李林峰 on 2018/8/11.
 */
public class LoadRunnerWaterClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;

    Runnable loadRunner;

    AtomicLong sendSum = new AtomicLong(0);

    Runnable profileMonitor;

    static Logger LOG = Logger.getLogger(LoadRunnerWaterClientHandler.class.getName());

    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    /**
     * Creates a client-side handler.
     */
    public LoadRunnerWaterClientHandler() {
        firstMessage = Unpooled.buffer(SIZE);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.channel().config().setWriteBufferHighWaterMark(10 * 1024 * 1024);
        loadRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ByteBuf msg = null;
                while (true) {
                    if (ctx.channel().isWritable()) {
                        msg = Unpooled.wrappedBuffer("Netty OOM Example".getBytes());
                        ctx.writeAndFlush(msg);
                    } else {
                        //获取积压消息个数

                        //问题1：ctx.channel().unsafe().outboundBuffer().size()实际上计算的是上次flushed时，发送队列中积压的消息，如果每次调用ctx.write（）时都调用一次ctx.flush()，则ctx.channel().unsafe().outboundBuffer().size()是准确的，如果周期性执行或者批量发送，write和flush不是成对出现的，则通过该方法获取的积压消息个数不精确。
                        //问题2：无法计算积压消息的内存占用，因为不同业务消息编码之后的长度不确定，因此，无法计算积压消息的实际内存占用。
                        //https://mp.weixin.qq.com/s/KeL_I0Na-DpX49Ka32IS9A?  Netty流控小技巧,李林峰写的内容
                        LOG.warning("The write queue is busy : " + ctx.channel().unsafe().outboundBuffer().nioBufferSize());
                        System.out.println("The write queue is busy : " + ctx.channel().unsafe().outboundBuffer().totalPendingWriteBytes());
                        System.out.println("The write queue is busy : " + ctx.channel().unsafe().outboundBuffer().nioBufferSize());
                    }
                }
            }
        };
        new Thread(loadRunner, "LoadRunner-Thread").start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
