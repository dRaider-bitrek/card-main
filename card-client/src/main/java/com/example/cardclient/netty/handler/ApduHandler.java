package com.example.cardclient.netty.handler;

import com.example.cardclient.service.ApduService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
@ChannelHandler.Sharable

public class ApduHandler extends ChannelInboundHandlerAdapter {
    private final ApduService apduService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String stringMessage = (String) msg;

        log.info("[Recive]: {}",stringMessage);

        if(stringMessage.contains("START")) {
           String atr = apduService.start();
            ctx.writeAndFlush(atr+"\r\n");
        }
        if (stringMessage.contains("CMD")){
            String atr=  apduService.command(stringMessage);
            ctx.writeAndFlush(atr+"\r\n");
        }
        super.channelRead(ctx, msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
