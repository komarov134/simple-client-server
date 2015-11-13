package ru.neron.education.simpleserver.client;

import ru.qatools.properties.Property;
import ru.qatools.properties.Resource;

/**
 * Created by neron on 12.11.15.
 */
@Resource.Classpath("client.properties")
public interface ClientProperties {

    @Property("client.serverPort")
    int getServerPort();

    @Property("client.serverAddress")
    String getServerAddress();

    @Property("client.threadsCount")
    int getThreadsCount();

    @Property("client.messageIntervalInMillis")
    int getMessageIntervalInMillis();

    @Property("client.messageLength")
    int getMessageLength();

}
