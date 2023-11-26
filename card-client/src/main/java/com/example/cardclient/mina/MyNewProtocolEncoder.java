package com.example.cardclient.mina;

//import com.example.minaspringtest.entity.MyProtocolMessage;
//import com.example.cardspringclient.entity.MyProtocolMessage;
import com.example.cardclient.entity.CardProtocolMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
//import com.example.minaspringtest.entity.ProtocolMessage;

public class MyNewProtocolEncoder extends ProtocolEncoderAdapter {
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        CardProtocolMessage protocolMessage = (CardProtocolMessage) message;
        IoBuffer buffer = IoBuffer.allocate(protocolMessage.getLength()+4,false);
        buffer.put(protocolMessage.getService());
        buffer.put(protocolMessage.getCommand());
        buffer.putShort(protocolMessage.getLength());
        buffer.put(protocolMessage.getData());
        buffer.flip();
        out.write(buffer);
    }

//    @Override
//    public void dispose(IoSession session) throws Exception {
//
//    }
}
