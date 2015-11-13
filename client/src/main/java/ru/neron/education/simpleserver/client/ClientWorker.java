package ru.neron.education.simpleserver.client;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static ru.neron.education.simpleserver.client.Client.PROPS;

/**
 * Created by neron on 12.11.15.
 */
public class ClientWorker implements Runnable {

    private final static Logger logger = Logger.getLogger(ClientWorker.class);

    private int serverPort;
    private InetAddress serverAddress;
    private AtomicLong counter;
    private int intervalInMillis = PROPS.getMessageIntervalInMillis();

    public ClientWorker(int serverPort, InetAddress serverAddress, AtomicLong counter) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.counter = counter;
    }

    public void run() {
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                dos.writeUTF(generateMessage(PROPS.getMessageLength()));
                dos.flush();
                long counterValue = counter.incrementAndGet();
                someWork(intervalInMillis);
                String serverResponse = dis.readUTF();
                logger.info(counterValue + " SERVER RESPONSE: " + serverResponse);
            }
        } catch (IOException ioe) {
            logger.error("Error while create socket OR while communicate with server", ioe);
        }
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Random rnd = ThreadLocalRandom.current();

    // generate string
    public String generateMessage(final int length) {
        StringBuilder sb = new StringBuilder(length);
        for( int i = 0; i < length; i++ ) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    // emulate some work for a time
    public static void someWork(final int timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            logger.error("Interrupt when sleep", e);
        }
    }
}
