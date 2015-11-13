package ru.neron.education.simpleserver.server;

import ru.qatools.properties.Property;
import ru.qatools.properties.Resource;

/**
 * Created by neron on 12.11.15.
 */
@Resource.Classpath("server.properties")
public interface ServerProperties {

    @Property("server.port")
    int getPort();

    @Property("server.address")
    String getAddress();

    @Property("server.delayTime")
    int getDelayTime();
}
