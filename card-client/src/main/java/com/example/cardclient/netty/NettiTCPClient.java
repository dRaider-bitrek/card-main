package com.example.cardclient.netty;

import com.example.cardclient.netty.handler.ApduHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//@Component
@Slf4j
@RequiredArgsConstructor
public class NettiTCPClient {
    private final String HOST = "localhost";
    private final int PORT = 8585;
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private final StringEncoder stringEncoder = new StringEncoder();
    private final StringDecoder stringDecoder = new StringDecoder();
    private Bootstrap b;
    private final ApduHandler apduHandler;

    @PostConstruct
    public void start() {
        log.info("[Server Start]");
        b = new Bootstrap(); // (1)
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(4096));
                ch.pipeline().addLast(stringDecoder);
                ch.pipeline().addLast(stringEncoder);
                ch.pipeline().addLast(apduHandler);
            }
        });
    }
    @PreDestroy
    public void stop(){
        workerGroup.shutdownGracefully();
    }

    @SneakyThrows
    public Channel connect(String host, int port){
        var channel= b.connect(host, port).sync().channel();
        log.info("Connect to channel {} {}:{}",channel,host,port );
        channel.writeAndFlush("LOGIN\r\n");
        return channel;
    }



}
