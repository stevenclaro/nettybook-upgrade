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
package io.netty.cases.chapter.demo3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 李林峰 on 2018/8/5.
 * 本类与V2版本差异主要在类中的成员变量实例化在类中，这样每个类都要进行这样的实例化。所以内存需要多
 * 另外一个是msg是在
 */

public class RouterServerHandler extends ChannelInboundHandlerAdapter {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf reqMsg = (ByteBuf)msg;
        byte [] body = new byte[reqMsg.readableBytes()];
//        ReferenceCountUtil.release(reqMsg);
        executorService.execute(()->
        {
            //解析请求消息，做路由转发，代码省略...
            //转发成功，返回响应给客户端
            //分配一个堆的内存区
            ByteBuf respMsg = allocator.heapBuffer(body.length);
            //对该内存区进行赋值
            respMsg.writeBytes(body);//作为示例，简化处理，将请求返回
            //对该内存区进行写操作
            ctx.writeAndFlush(respMsg);
        });
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
