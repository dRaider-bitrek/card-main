package com.example.cardclient.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class PassiveKeepAliveMessageFactory implements KeepAliveMessageFactory {
    static final IoBuffer PING = IoBuffer.wrap(new byte[]{3, 9, 0, 0});
    static final IoBuffer PONG = IoBuffer.wrap(new byte[]{4, 9, 0, 0});


    @Override
    public boolean isRequest(IoSession session, Object message) {
//        System.out.println("isRequest");
        if (message instanceof IoBuffer) {
            return checkRequest((IoBuffer) message);
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
//        System.out.println("isResponse");
        if (message instanceof IoBuffer) {
            return checkResponse((IoBuffer) message);
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession session) {
//      return  session.getConfig().getBothIdleTime();
//        session.write(String.format("You connected %d seconds ago", (System.currentTimeMillis() - (Long) session.getAttribute("time")) / 1000));
//        System.out.println("getRequest");
//        session.closeOnFlush();
        return PING.duplicate();

    }

    @Override
    public Object getResponse(IoSession session, Object request) {
//        System.out.println("getResponse");

        return null;
    }

    static boolean checkRequest(IoBuffer message) {
        IoBuffer buff = message;
        boolean check = buff.get() == 3;
        buff.rewind();
        return check;
    }

    static boolean checkResponse(IoBuffer message) {
        IoBuffer buff = message;
        boolean check = buff.get() == 4;
        buff.rewind();
        return check;
    }
}
