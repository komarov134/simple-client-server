package ru.neron.education.simpleserver.client;

import org.apache.log4j.Logger;
import ru.qatools.properties.PropertyLoader;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by neron on 12.11.15.
 */
public class Client {

    public final static ClientProperties PROPS = PropertyLoader.newInstance().populate(ClientProperties.class);
    private final static Logger logger = Logger.getLogger(Client.class);
    private final AtomicLong counter = new AtomicLong();
    private final int serverPort;
    private InetAddress serverAddress;

    public static final Client INSTANCE = new Client();

    private Client() {
        this.serverPort = PROPS.getServerPort();
        try {
            this.serverAddress = InetAddress.getByName(PROPS.getServerAddress());
        } catch (UnknownHostException e) {
            logger.error("Error while create InetAddress", e);
            System.exit(1);
        }
    }

    public void send() {
        createShutDownHook();
        for (int i = 0; i < PROPS.getThreadsCount(); i++) {
            new Thread(new ClientWorker(serverPort, serverAddress, counter)).start();
        }
    }

    public void createShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Sent out messages = " + counter.get());
            }
        }));
    }

    public static void main(String... args) {
        Client.INSTANCE.send();
    }
}