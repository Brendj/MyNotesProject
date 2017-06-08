/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ThreadDump;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * Created by i.semenov on 05.05.2017.
 */
@Component
@Scope(value = "singleton")
public class ThreadDumpService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ThreadDumpService.class);
    ThreadMXBean threadMxBean;

    public static final String NEWLINE = System.getProperty("line.separator");
    private boolean isON;
    private static final int COUNT_PROBLEM_STACKS = 3;

    @PostConstruct
    public void init() {
        threadMxBean = ManagementFactory.getThreadMXBean();
        isON = isOn();
    }

    public void run() {
        if (isON && threadMxBean.isCurrentThreadCpuTimeSupported()) {
            ThreadDumpInfo dump = dumpStack();
            saveDump(dump);
        }
    }

    private boolean isOn() {
        String nodes = RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.thread.dump.nodes", "");
        if (nodes.equals("ALL")) {
            return true;
        } else if (nodes.equals("")) {
            return false;
        }
        String[] strs = nodes.split(",");
        List<String> nodesList = new ArrayList<String>(Arrays.asList(strs));
        if (nodesList.contains(RuntimeContext.getInstance().getNodeName()))
            return true;
        else
            return false;
    }

    public ThreadDumpInfo dumpStack() {
        Long duration = System.currentTimeMillis();
        ThreadDumpInfo result = new ThreadDumpInfo();
        result.setNode(RuntimeContext.getInstance().getNodeName());
        result.setDateTime(new Date());

        Map<Long, ThreadInfo> threadInfoMap = new HashMap<Long, ThreadInfo>();

        ThreadInfo[] threadInfos = threadMxBean.getThreadInfo(threadMxBean.getAllThreadIds(), 0);
        for (ThreadInfo threadInfo : threadInfos) {
            threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
        }

        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        try {
            Map<String, Integer> problemThreads = new HashMap<String, Integer>(); //сюда повторяющиеся дампы потоков
            List<ThreadDumpInfoItem> items = new ArrayList<ThreadDumpInfoItem>();
            long totalCPU = 0;
            for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
                boolean axettaThreads = false;
                Thread thread = entry.getKey();
                ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
                if (threadInfo == null) continue;

                if (thread.getId() == Thread.currentThread().getId()) {
                    continue; //если поток - этот самый, его пропускаем
                }

                StringBuilder traceEntry = new StringBuilder();
                for (StackTraceElement element : entry.getValue()) {
                    traceEntry.append("    ");
                    String eleStr = element.toString();
                    if (eleStr.startsWith("ru.axetta") && !eleStr.contains(".doFilter")) {
                        axettaThreads = true;
                        traceEntry.append(">>  ");
                    } else {
                        traceEntry.append("    ");
                    }
                    traceEntry.append(eleStr);
                    traceEntry.append(NEWLINE);
                }
                traceEntry.append(NEWLINE);

                if (!axettaThreads) continue; //потоки не с нашими стеками пропускаем

                totalCPU += threadMxBean.getThreadCpuTime(thread.getId()) / 1000000L;

                StringBuilder sb = new StringBuilder();
                ThreadDumpInfoItem item = new ThreadDumpInfoItem();
                item.setThreadName(thread.getName());
                item.setThreadCPUTime(threadMxBean.getThreadCpuTime(thread.getId()) / 1000000L);
                sb.append(String.format("name=%s prio=%s tid=%s %s %s time=%s" + NEWLINE, thread.getName(), thread.getPriority(),
                        thread.getId(), thread.getState(), (thread.isDaemon() ? "deamon" : "worker"), threadMxBean.getThreadCpuTime(thread.getId()) / 1000000L));
                sb.append(String.format("    native=%s, suspended=%s, block=%s, wait=%s" + NEWLINE, threadInfo.isInNative(),
                        threadInfo.isSuspended(), threadInfo.getBlockedCount(), threadInfo.getWaitedCount()));
                sb.append(String.format("    lock=%s  owned by (%s), cpu=%s, user=%s" + NEWLINE, threadInfo.getLockName(), threadInfo.getLockOwnerName(),
                        Long.toString(threadMxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L),
                        Long.toString(threadMxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L)));

                String traceStr = traceEntry.toString();
                Integer count = problemThreads.get(traceStr);
                if (count == null) {
                    problemThreads.put(traceStr, 1);
                } else {
                    problemThreads.put(traceStr, count+1);
                }

                sb.append(traceEntry.toString());
                item.setDumpStack(sb.toString());
                items.add(item);
            }

            String pThreads = "";
            CollectionUtils.sortByValue(problemThreads);
            for (Map.Entry<String, Integer> th : problemThreads.entrySet()) {
                if (th.getValue() < COUNT_PROBLEM_STACKS) continue;
                pThreads += String.format("Count: %s", th.getValue()) + NEWLINE + th.getKey() + NEWLINE;
            }
            result.setProblemStacks(pThreads);
            result.setTotalCPUTime(totalCPU);
            Collections.sort(items);
            result.setThreads(items);
        } catch (Exception e) {
            logger.error("Error in threadDumpService:", e);
            return null;
        }
        duration = System.currentTimeMillis() - duration;
        result.setDuration(duration); //сколько длился этот подсчет в мс
        return result;
    }

    public void saveDump(ThreadDumpInfo info) {
        if (StringUtils.isEmpty(info.getThreadDump())) return;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            ThreadDump dump = new ThreadDump();
            dump.setDateTime(info.getDateTime());
            dump.setProblemStacks(info.getProblemStacks());
            dump.setNode(info.getNode());
            dump.setDuration(info.getDuration());
            dump.setTotalCPUTime(info.getTotalCPUTime());
            dump.setDumpStack(info.getThreadDump());
            session.persist(dump);
            transaction.commit();
            transaction = null;
        }  catch (Exception e) {
            logger.error("Error saving record to ThreadDump: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static class ThreadDumpInfo {
        private Date dateTime;
        private String node;
        private Long totalCPUTime;
        private Long duration;
        private String problemStacks;
        private List<ThreadDumpInfoItem> threads;

        public String getThreadDump() {
            String result = "";
            for (ThreadDumpInfoItem item : threads) {
                result += item.getDumpStack();
            }
            return result;
        }

        public List<ThreadDumpInfoItem> getThreads() {
            return threads;
        }

        public void setThreads(List<ThreadDumpInfoItem> threads) {
            this.threads = threads;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }

        public Long getTotalCPUTime() {
            return totalCPUTime;
        }

        public void setTotalCPUTime(Long totalCPUTime) {
            this.totalCPUTime = totalCPUTime;
        }

        public Date getDateTime() {
            return dateTime;
        }

        public void setDateTime(Date dateTime) {
            this.dateTime = dateTime;
        }

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getProblemStacks() {
            return problemStacks;
        }

        public void setProblemStacks(String problemStacks) {
            this.problemStacks = problemStacks;
        }
    }

    public static class ThreadDumpInfoItem implements Comparable<ThreadDumpInfoItem> {
        private Long threadCPUTime;
        private String dumpStack;
        private String threadName;

        @Override
        public int compareTo(ThreadDumpInfoItem o) {
            return -threadCPUTime.compareTo(o.getThreadCPUTime());
        }

        public Long getThreadCPUTime() {
            return threadCPUTime;
        }

        public void setThreadCPUTime(Long threadCPUTime) {
            this.threadCPUTime = threadCPUTime;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public String getDumpStack() {
            return dumpStack;
        }

        public void setDumpStack(String dumpStack) {
            this.dumpStack = dumpStack;
        }
    }
}
