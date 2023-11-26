package com.example.cardclient.service;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.smartcardio.*;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Data
public class ApduService {
    TerminalFactory factory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = new ArrayList<>();
    CardChannel channel;
    Channel serverChannel;
    Card card;


    public String getSerialNumber() {
        try {
            start();
//            startBin();
            command("CMD:00A4020C020002");
            var result = command("CMD:00B0000009").replaceAll("APDU:", "").substring(2, 18);
            close();
            return result;
        } catch (CardException e) {
            return null;
        }
    }

    public boolean isCardPresent(){
        try {
          return terminals.get(0).isCardPresent();
        } catch (CardException e) {
            return false;
        }
    }


    public CardTerminal getTerminal() throws Exception {


        TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        System.out.println(factory);

        List<CardTerminal> terminals = factory.terminals().list();
        System.out.println("Terminals: " + terminals);
        if (terminals.isEmpty()) {
            throw new Exception("No card terminals available");
        }
        CardTerminal terminal = terminals.get(0);

        if (terminal.isCardPresent() == false) {
            System.out.println("*** Insert card");
            if (terminal.waitForCardPresent(20 * 1000) == false) {
                throw new Exception("no card available");
            }
        }
        return terminal;
    }


    public String start() throws CardException {
        close();
//        factory = TerminalFactory.getDefault();

//        terminals = factory.terminals().list();
//        clearContext();
        var temp = TerminalFactory.getDefault().terminals();

         terminals = temp.list();


        if (terminals.size() < 1) {
            throw new CardException("No attached Smart Card terminals found");
        }

        terminals.forEach(x -> log.info("Terminal: {}", x.getName()));

        for (CardTerminal terminal : terminals) {
            if (terminal.isCardPresent()) {
                card = terminal.connect("T=1");
                break;
            }
        }
        if (card == null) {
            throw new CardException("No connected Smart Cards found");
        }

//        var terminal = terminals.get(0);

//        System.out.printf("Using terminal %s%n", terminal.toString());
//        terminal.waitForCardPresent(1000);
//        card = terminal.connect("T=1");
        String atr = hexify(card.getATR().getBytes());
        log.info("  Card ATR: {}; {}", atr);
        channel = card.getBasicChannel();
        log.info("  Card Protocol: {}", card.getProtocol());

        return "ATR:" + atr;
    }

    public byte[] startBin(IoSession session) throws CardException {
        log.info("startBin");
        close();

        terminals = factory.terminals().list();
        terminals.forEach(x -> log.info("Terminal: {}", x.getName()));

        var terminal = terminals.get(0);
//        runWatch(terminal,session);
        System.out.printf("Using terminal %s%n", terminal.toString());
        terminal.waitForCardPresent(1000);
        card = terminal.connect("T=1");
        String atr = hexify(card.getATR().getBytes());
        log.info("  Card ATR: {}; {}", atr);
        channel = card.getBasicChannel();
        log.info("  Card Protocol: {}", card.getProtocol());

        return card.getATR().getBytes();
    }

    public void close() {
        try {
            if (card != null) card.disconnect(true);
        } catch (CardException e) {

        }
    }

    public static String hexify(byte[] bytes) {
        var bytesStrings = new ArrayList<String>(bytes.length);
        for (var b : bytes) {
            bytesStrings.add(String.format("%02X", b));
        }
        return String.join("", bytesStrings);
    }

    @SneakyThrows
    public String command(String stringMessage) {
        log.info(stringMessage);
        var arr = toByteArray(stringMessage.replaceAll("CMD:", ""));
        System.out.println();
        CommandAPDU command = new CommandAPDU(arr);
        ResponseAPDU answer = channel.transmit(command);
        log.info("Send command: {}", stringMessage);
        log.info("answer r UID: " + answer.toString()); // response
        log.info("Card UID: {}", hexify(answer.getBytes()));
        System.out.println();
        var result = hexify(answer.getBytes());
//       if (result.equals("6E00")||result.equals("6688")) result = "9000";

        return "APDU:" + result;
    }

    @SneakyThrows
    public byte[] commandBin(byte[] byteMessage) {
//        log.info(stringMessage);
//        var arr= toByteArray(stringMessage.replaceAll("CMD:",""));
        System.out.println();
        CommandAPDU command = new CommandAPDU(byteMessage);
        ResponseAPDU answer = channel.transmit(command);
        log.info("Send command: {}", hexify(byteMessage));
        log.info("answer r UID: " + answer.toString()); // response
        log.info("Card UID: {}", hexify(answer.getBytes()));
        System.out.println();
//        var result = hexify(answer.getBytes());
//       if (result.equals("6E00")||result.equals("6688")) result = "9000";

        return answer.getBytes();
    }

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    @Scheduled(fixedRate = 60000)
    public void pingScheduled() {
//        System.out.println("PING");
        if (serverChannel != null) serverChannel.writeAndFlush("PING\r\n");
    }

    public void runWatch(CardTerminal term,IoSession session){

        Thread watch = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("START CARD MONITOR");
                term.waitForCardAbsent(24*60*60*1000);

                System.out.println("CARD NOT PRESENT!!!!!");
                session.closeNow();

            }
        });
        watch.setDaemon(true);
        watch.start();
    }


//    @SneakyThrows
//    private void clearContext(){
//        Class pcscterminal = Class.forName("sun.security.smartcardio.PCSCTerminals");
//        Field contextId = pcscterminal.getDeclaredField("contextId");
//        contextId.setAccessible(true);
//
//        if(contextId.getLong(pcscterminal) != 0L)
//        {
//            Class pcsc = Class.forName("sun.security.smartcardio.PCSC");
//            Method SCardEstablishContext = pcsc.getDeclaredMethod(
//                    "SCardEstablishContext",
//                    new Class[] {Integer.TYPE }
//            );
//            SCardEstablishContext.setAccessible(true);
//
//            Field SCARD_SCOPE_USER = pcsc.getDeclaredField("SCARD_SCOPE_USER");
//            SCARD_SCOPE_USER.setAccessible(true);
//
//            long newId = ((Long)SCardEstablishContext.invoke(pcsc,
//                    new Object[] { Integer.valueOf(SCARD_SCOPE_USER.getInt(pcsc)) }
//            )).longValue();
//            contextId.setLong(pcscterminal, newId);
//        }
//    }

}
