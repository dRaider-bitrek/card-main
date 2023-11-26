package com.example.cardclient.mina;

//import com.example.minaspringtest.entity.ProtocolMessage;
//import com.example.minaspringtest.entity.MyProtocolMessage;
//import com.example.cardspringclient.entity.MyProtocolMessage;
import com.example.cardclient.entity.CardProtocolMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyNewCumulativeProtocolDecode extends CumulativeProtocolDecoder{
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        if(in.remaining() >= 3 ){
            var sa = in.get();
            var cmd = in.get();
            var length= in.getShort();
            if (in.remaining() >= length){
                byte[] result = new byte[length];
                in.get(result);
                var message = new CardProtocolMessage(sa,cmd,length,result);
            out.write(message);
            return true;
            }
        }
        in.rewind();
        return false;
    }
}
