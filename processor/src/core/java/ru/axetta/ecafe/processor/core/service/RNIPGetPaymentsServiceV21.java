/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.RNIPVersion;
import ru.axetta.ecafe.processor.core.persistence.RnipMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by i.semenov on 26.11.2019.
 */
@Component
@Scope("singleton")
public class RNIPGetPaymentsServiceV21 {
    private final static ThreadLocal<Logger> logger = new ThreadLocal<Logger>(){
        @Override protected Logger initialValue() { return LoggerFactory.getLogger(RNIPGetPaymentsServiceV21.class); }
    };
    private static final Set<Long> rnipMessagesInProgress = Collections.synchronizedSet(new HashSet<Long>());

    @Resource(name = "rnipPaymentsExecutor")
    protected TaskExecutor taskExecutor;

    public RNIPGetPaymentsServiceV21() {

    }

    public void processRnipMessage(RnipMessage rnipMessage) {
        RnipGetPaymentsV21ThreadWrapper wrapper = new RnipGetPaymentsV21ThreadWrapper(rnipMessage);
        if (wrapper.getRnipMessage() != null) taskExecutor.execute(wrapper);
    }

    public int getActiveCount() {
        return ((ThreadPoolTaskExecutor)taskExecutor).getActiveCount();
    }

    public int getRemainingCapacity() {
        return ((ThreadPoolTaskExecutor)taskExecutor).getThreadPoolExecutor().getQueue().remainingCapacity();
    }

    public static final class RnipGetPaymentsV21ThreadWrapper implements Runnable {
        private RnipMessage rnipMessage;

        public RnipGetPaymentsV21ThreadWrapper(RnipMessage rnipMessage) {
            this.rnipMessage = null;
            synchronized (rnipMessagesInProgress) {
                boolean exists = false;
                for (Long rm : rnipMessagesInProgress) {
                    if (rnipMessage.getIdOfRnipMessage().equals(rm)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    this.rnipMessage = rnipMessage;
                    rnipMessagesInProgress.add(rnipMessage.getIdOfRnipMessage());
                }
            }

        }

        @Override
        public void run() {
            try {
                logger.get().info(String.format("Start RNIP get payments task. RnipMessage id = %s, running threads count = %s, available in queue = %s",
                        rnipMessage.getIdOfRnipMessage(),
                        RuntimeContext.getAppContext().getBean(RNIPGetPaymentsServiceV21.class).getActiveCount(),
                        RuntimeContext.getAppContext().getBean(RNIPGetPaymentsServiceV21.class).getRemainingCapacity()));
                RNIPLoadPaymentsServiceV21 service = getRNIPServiceBean();
                service.processRnipMessage(rnipMessage);
            } catch (Exception e) {
                logger.get().error("Error in processing rnip message async", e);
                synchronized (rnipMessagesInProgress) {
                    for (Iterator<Long> iterator = rnipMessagesInProgress.iterator(); iterator.hasNext(); ) {
                        Long rm = iterator.next();
                        if (rm.equals(rnipMessage.getIdOfRnipMessage())) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }

        public static RNIPLoadPaymentsServiceV21 getRNIPServiceBean() {
            RNIPVersion version = RNIPVersion.getType(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION));
            switch (version) {
                case RNIP_V21:
                    return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV21", RNIPLoadPaymentsServiceV21.class);
                case RNIP_V22:
                    return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV22", RNIPLoadPaymentsServiceV22.class);
            }
            return null;
        }

        public RnipMessage getRnipMessage() {
            return rnipMessage;
        }

        public void setRnipMessage(RnipMessage rnipMessage) {
            this.rnipMessage = rnipMessage;
        }
    }
}
