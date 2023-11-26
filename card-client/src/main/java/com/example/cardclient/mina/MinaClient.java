package com.example.cardclient.mina;

//import com.example.cardspringclient.entity.MyProtocolMessage;


import com.example.cardclient.config.LoadProperties;
import com.example.cardclient.entity.CardProtocolMessage;
import com.example.cardclient.service.RestartService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

import static org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler.CLOSE;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinaClient {

    private NioSocketConnector connector;
    IoSession session;
//private IoAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);

    private final MyMinaServerHandler myMinaServerHandler;
    private final RestartService restartService;
    private final LoadProperties loadProperties;

    @SneakyThrows
    public boolean init(String id) {
//       KeepAliveFilter filter = new KeepAliveFilter(new ClientFactory(), status, EXCEPTION, INTERVAL, TIMEOUT);
        try {
            connector = new NioSocketConnector();
            KeepAliveFilter filter = new KeepAliveFilter(new PassiveKeepAliveMessageFactory(),
                    IdleStatus.BOTH_IDLE, CLOSE, 60, 30);
            filter.setForwardEvent(true);
            connector.getFilterChain().addLast("keep-alive", filter);

            connector.getFilterChain().addLast("cumprotocol", new ProtocolCodecFilter(new MyCodecFactory()));

//        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(
//                new TextLineCodecFactory(Charset.defaultCharset(),System.lineSeparator(),System.lineSeparator())));

//        acceptor.getFilterChain().addLast("keepAlive",new KeepAliveFilter(new PassiveKeepAliveMessageFactory()));
//        acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE,15);
            restartService.setMinaClient(this);
            myMinaServerHandler.setRestartService(restartService);

            connector.setHandler(myMinaServerHandler);
            ConnectFuture future = connector.connect(new InetSocketAddress(loadProperties.getUrl(), 5558)).awaitUninterruptibly();
//
//            ConnectFuture future = connector.connect(new InetSocketAddress("localhost",5558)).awaitUninterruptibly();
//        acceptor.bind(new InetSocketAddress(5558));
            session = future.getSession();
            session.write(new CardProtocolMessage((byte) 2, (byte) 23, (byte) (id.length()),
                   id.getBytes()));
//            session.write(new CardProtocolMessage((byte) 2, (byte) 23, (byte) 5,
//                    loadProperties.getUuid().getBytes()));
            log.info("Server start on ...");
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @PreDestroy
    public void tearDown() throws Exception {
        try {
            session.closeNow();
        } catch (Exception exception) {
        }
        try {
            connector.dispose();
        } catch (Exception exception) {
        }
    }
}
