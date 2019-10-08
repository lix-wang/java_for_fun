package com.xiao.demo.server.demo.rpc;

import okhttp3.Call;
import okhttp3.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;

/**
 * Demo eventListener.
 *
 * @author lix wang
 */
public class DemoEventListener extends EventListener {
    private final static Logger logger = LogManager.getLogger(DemoEventListener.class);
    private long startTime;

    @Override
    public void callEnd(@NotNull Call call) {
        printProcessLog("callEnd");
    }

    @Override
    public void callStart(@NotNull Call call) {
        startTime = System.currentTimeMillis();
        printProcessLog("callStart");
    }

    @Override
    public void dnsEnd(@NotNull Call call, @NotNull String domainName, @NotNull List<InetAddress> inetAddressList) {
        printProcessLog("dnsEnd");
    }

    @Override
    public void dnsStart(@NotNull Call call, @NotNull String domainName) {
        printProcessLog("dnsStart");
    }

    private void printProcessLog(String name) {
        logger.info("Process to " + name + " consume " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
