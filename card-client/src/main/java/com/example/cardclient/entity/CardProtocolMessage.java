package com.example.cardclient.entity;

import lombok.Data;

@Data
//@AllArgsConstructor
public class CardProtocolMessage {

    private byte service;
    private byte command;
    private short length;
    private byte[] data;


    public CardProtocolMessage( byte service, byte command, short length) {
        this.service = service;
        this.command = command;
        this.length = length;
        this.data = new byte[]{};
    }

    public CardProtocolMessage(byte service, byte command, short length, byte[] data) {
        this.service = service;
        this.command = command;
        this.length = length;
        this.data = data;
    }

    //    @Override
//    public String toString() {
//        return "ProtocolMessage{" +
//                "service=" + service +
//                ", command=" + command +
//                ", length=" + length +
//                ", data=" + Arrays.toString(data) +
//                '}';
//    }
}
