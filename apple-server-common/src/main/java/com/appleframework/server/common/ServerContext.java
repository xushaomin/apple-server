package com.appleframework.server.common;

import com.appleframework.server.spi.Context;
import com.appleframework.server.spi.common.CacheManager;
import com.appleframework.server.spi.common.MQClient;
import com.appleframework.server.srd.ServiceDiscovery;
import com.appleframework.server.srd.ServiceRegistry;

public interface ServerContext extends Context {

	Monitor getMonitor();

    ServiceDiscovery getDiscovery();

    ServiceRegistry getRegistry();

    CacheManager getCacheManager();

    MQClient getMQClient();
}
