package com.appleframework.server.tools.thread.pool;

import java.util.concurrent.*;

import com.appleframework.server.spi.Spi;
import com.appleframework.server.spi.common.ExecutorFactory;
import com.appleframework.server.tools.config.CC;
import com.appleframework.server.tools.log.Logs;
import com.appleframework.server.tools.thread.NamedPoolThreadFactory;

import static com.appleframework.server.tools.config.CC.mp.thread.pool.ack_timer;
import static com.appleframework.server.tools.config.CC.mp.thread.pool.push_client;
import static com.appleframework.server.tools.thread.ThreadNames.*;

/**
 * 此线程池可伸缩，线程空闲一定时间后回收，新请求重新创建线程
 */
@Spi(order = 1)
public final class DefaultExecutorFactory implements ExecutorFactory {

    private Executor get(ThreadPoolConfig config) {
        String name = config.getName();
        int corePoolSize = config.getCorePoolSize();
        int maxPoolSize = config.getMaxPoolSize();
        int keepAliveSeconds = config.getKeepAliveSeconds();
        BlockingQueue<Runnable> queue = config.getQueue();

        return new DefaultExecutor(corePoolSize
                , maxPoolSize
                , keepAliveSeconds
                , TimeUnit.SECONDS
                , queue
                , new NamedPoolThreadFactory(name)
                , new DumpThreadRejectedHandler(config));
    }

    @Override
    public Executor get(String name) {
        final ThreadPoolConfig config;
        switch (name) {
            case EVENT_BUS:
                config = ThreadPoolConfig
                        .build(T_EVENT_BUS)
                        .setCorePoolSize(CC.mp.thread.pool.event_bus.min)
                        .setMaxPoolSize(CC.mp.thread.pool.event_bus.max)
                        .setKeepAliveSeconds(TimeUnit.SECONDS.toSeconds(10))
                        .setQueueCapacity(CC.mp.thread.pool.event_bus.queue_size)
                        .setRejectedPolicy(ThreadPoolConfig.REJECTED_POLICY_CALLER_RUNS);
                break;
            case MQ:
                config = ThreadPoolConfig
                        .build(T_MQ)
                        .setCorePoolSize(CC.mp.thread.pool.mq.min)
                        .setMaxPoolSize(CC.mp.thread.pool.mq.max)
                        .setKeepAliveSeconds(TimeUnit.SECONDS.toSeconds(10))
                        .setQueueCapacity(CC.mp.thread.pool.mq.queue_size)
                        .setRejectedPolicy(ThreadPoolConfig.REJECTED_POLICY_CALLER_RUNS);
                ;
                break;
            case PUSH_CLIENT: {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(push_client
                        , new NamedPoolThreadFactory(T_PUSH_CLIENT_TIMER), (r, e) -> r.run() // run caller thread
                );
                executor.setRemoveOnCancelPolicy(true);
                return executor;
            }
            /*case PUSH_TASK:
                return new ScheduledThreadPoolExecutor(push_task, new NamedPoolThreadFactory(T_PUSH_CENTER_TIMER),
                        (r, e) -> {
                            throw new PushException("one push task was rejected. task=" + r);
                        }
                );*/
            case ACK_TIMER: {
                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(ack_timer,
                        new NamedPoolThreadFactory(T_ARK_REQ_TIMER),
                        (r, e) -> Logs.PUSH.error("one ack context was rejected, context=" + r)
                );
                executor.setRemoveOnCancelPolicy(true);
                return executor;
            }
            default:
                throw new IllegalArgumentException("no executor for " + name);
        }

        return get(config);
    }
}

