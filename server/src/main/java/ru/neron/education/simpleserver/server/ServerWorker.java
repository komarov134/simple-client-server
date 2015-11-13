package ru.neron.education.simpleserver.server;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

import static ru.neron.education.simpleserver.server.Server.PROPS;

/**
 * Created by neron on 12.11.15.
 */
public class ServerWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(ServerWorker.class);
    private final Socket clientSocket;
    private final AtomicLong counter;
    private static final int delayTimeInMillis = PROPS.getDelayTime();


    public ServerWorker(final Socket clientSocket, final AtomicLong counter) {
        this.clientSocket = clientSocket;
        this.counter = counter;
    }

    public void run() {
        try (
                Socket socket = clientSocket;
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                String receivedMessage = dis.readUTF();
                System.out.println(counter.getAndIncrement() + " STORE: " + receivedMessage);
                someWork(delayTimeInMillis);
                dos.writeUTF("OK\n");
                dos.flush();
            }
        } catch (IOException ioe) {
            logger.error("Error while communicate with client", ioe);
        }
    }

    // emulate some work for a time
    public static void someWork(final int timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
