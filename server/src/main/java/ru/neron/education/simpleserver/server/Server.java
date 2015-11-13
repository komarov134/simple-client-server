package ru.neron.education.simpleserver.server;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;
import ru.qatools.properties.PropertyLoader;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.server.ExportException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by neron on 12.11.15.
 */
public class Server {

    public final static ServerProperties PROPS = PropertyLoader.newInstance().populate(ServerProperties.class);
    private final static Logger logger = Logger.getLogger(Server.class);
    private final AtomicLong counter = new AtomicLong();
    private final int port;
    private InetAddress inetAddress;
    private ExecutorService service;

    private Server() {
        this.service = Executors.newCachedThreadPool();
        this.port = PROPS.getPort();
        try {
            this.inetAddress = InetAddress.getByName(PROPS.getAddress());
        } catch (UnknownHostException e) {
            logger.error("Error while create InetAddress", e);
            System.exit(1);
        }
    }

    public static final Server INSTANCE = new Server();

    public void listen() {
        createShutDownHook();
        try (ServerSocket ss = new ServerSocket(port, 0, inetAddress)) {
            while (true) {
                try {
                    service.submit(new ServerWorker(ss.accept(), counter));
                } catch (IOException ioe) {
                    logger.error("Error while server socket accept", ioe);
                }
            }
        } catch (Exception e) {
            logger.error("Error while server socket initialize", e);
            System.exit(1);
        }
    }

    public void createShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Received messages = " + counter.get());
                List<Runnable> nonCommencedTasks = service.shutdownNow();
                logger.info("count of tasks that never commenced execution = " + nonCommencedTasks.size());
            }
        }));
    }

    public static void main(String... args) {
        Server.INSTANCE.listen();
    }
}
