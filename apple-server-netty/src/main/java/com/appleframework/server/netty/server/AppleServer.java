package com.appleframework.server.netty.server;

import java.util.HashMap;
import java.util.Map;

import com.appleframework.server.common.ServerContext;
import com.appleframework.server.common.ServerNodes;
import com.appleframework.server.event.EventBus;
import com.appleframework.server.monitor.service.MonitorService;
import com.appleframework.server.netty.http.HttpClient;
import com.appleframework.server.netty.http.NettyHttpClient;
import com.appleframework.server.netty.message.MessageHandler;
import com.appleframework.server.netty.protocol.Command;
import com.appleframework.server.spi.common.CacheManager;
import com.appleframework.server.spi.common.CacheManagerFactory;
import com.appleframework.server.spi.common.MQClient;
import com.appleframework.server.spi.common.MQClientFactory;
import com.appleframework.server.spi.common.ServiceDiscoveryFactory;
import com.appleframework.server.spi.common.ServiceRegistryFactory;
import com.appleframework.server.srd.ServiceDiscovery;
import com.appleframework.server.srd.ServiceNode;
import com.appleframework.server.srd.ServiceRegistry;

public class AppleServer implements ServerContext {

    private ServiceNode connServerNode;
    //private ServiceNode gatewayServerNode;
    //private ServiceNode websocketServerNode;

    private ConnectionServer connectionServer;
    /*private WebsocketServer websocketServer;
    private GatewayServer gatewayServer;
    private AdminServer adminServer;
    private GatewayUDPConnector udpGatewayServer;*/

    private HttpClient httpClient;

    /*private PushCenter pushCenter;

    private ReusableSessionManager reusableSessionManager;

    private RouterCenter routerCenter;*/

    private MonitorService monitorService;
    
    private Map<Byte, MessageHandler> handlers = new HashMap<>();
    
    private boolean heartbeatCheck;

    public AppleServer() {
        connServerNode = ServerNodes.cs();
        //gatewayServerNode = ServerNodes.gs();
        //websocketServerNode = ServerNodes.ws();

        monitorService = new MonitorService();
        EventBus.create(monitorService.getThreadPoolManager().getEventBusExecutor());

        /*reusableSessionManager = new ReusableSessionManager();

        pushCenter = new PushCenter(this);

        routerCenter = new RouterCenter(this);*/

        connectionServer = new ConnectionServer(this);

        /*websocketServer = new WebsocketServer(this);

        adminServer = new AdminServer(this);

        if (tcpGateway()) {
            gatewayServer = new GatewayServer(this);
        } else {
            udpGatewayServer = new GatewayUDPConnector(this);
        }*/
    }

    public boolean isTargetMachine(String host, int port) {
        //return port == gatewayServerNode.getPort() && gatewayServerNode.getHost().equals(host);
    	return false;
    }

    public ServiceNode getConnServerNode() {
        return connServerNode;
    }

    /*public ServiceNode getGatewayServerNode() {
        return gatewayServerNode;
    }

    public ServiceNode getWebsocketServerNode() {
        return websocketServerNode;
    }*/

    public ConnectionServer getConnectionServer() {
        return connectionServer;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = new NettyHttpClient();
                }
            }
        }
        return httpClient;
    }
    
    @Override
    public MonitorService getMonitor() {
        return monitorService;
    }

    public Map<Byte, MessageHandler> getHandlers() {
		return handlers;
	}

    /*public void setHandlers(Map<Byte, MessageHandler> handlers) {
		this.handlers = handlers;
	}*/
	
	public void register(Command command, MessageHandler handler) {
        handlers.put(command.cmd, handler);
    }

	@Override
    public ServiceDiscovery getDiscovery() {
        return ServiceDiscoveryFactory.create();
    }

    @Override
    public ServiceRegistry getRegistry() {
        return ServiceRegistryFactory.create();
    }

    @Override
    public CacheManager getCacheManager() {
        return CacheManagerFactory.create();
    }

    @Override
    public MQClient getMQClient() {
        return MQClientFactory.create();
    }

	public boolean isHeartbeatCheck() {
		return heartbeatCheck;
	}

	public void setHeartbeatCheck(boolean heartbeatCheck) {
		this.heartbeatCheck = heartbeatCheck;
	}
    
}
