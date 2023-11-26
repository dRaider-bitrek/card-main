package com.example.cardclient.mina;

import com.example.cardclient.entity.CardProtocolMessage;
import com.example.cardclient.service.ApduService;
import com.example.cardclient.service.FileRqestServise;
import com.example.cardclient.service.RestartService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;
//import com.example.minaspringtest.entity.ProtocolMessage;


@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class MyMinaServerHandler extends IoHandlerAdapter {
    private final ApduService apduService;
    private RestartService restartService;
    private final FileRqestServise fileRqestServise;

//    private final CardService cardService;



    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.setAttribute("time",System.currentTimeMillis());
//        session.write("Hello");
        log.info("Session Opened: {}",session.getId());
//        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("Session Closed: {}",session.getId());
        restartService.restartClient();
//        restartService.cTerminals();
//        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        CardProtocolMessage protocolMessage = (CardProtocolMessage)message;
        log.info("Recive: {}",protocolMessage);
        if(protocolMessage.getCommand()==8){
           var data= apduService.startBin(session);
           var newMessage = new CardProtocolMessage((byte)2,(byte)1,(short)data.length,data);
           session.write(newMessage);
        }else if(protocolMessage.getCommand()==33){
            fileRqestServise.getFile("862430053478109");
        }
        else{
            var data= apduService.commandBin(protocolMessage.getData());
            var newMessage = new CardProtocolMessage((byte)2,(byte)2,(short)data.length,data);
            session.write(newMessage);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.info("Session Idle: {}", session.getId());
        log.info(String.format("You connected %d seconds ago", (System.currentTimeMillis() - (Long) session.getAttribute("time")) / 1000));
        if (!apduService.isCardPresent()) session.closeNow();
        //        session.closeOnFlush();
//        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.info("Exception Caught: {}", session.getId());

//        super.exceptionCaught(session, cause);
    }
}
