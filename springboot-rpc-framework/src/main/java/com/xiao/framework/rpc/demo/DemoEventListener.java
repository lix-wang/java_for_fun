package com.xiao.framework.rpc.demo;

import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.EventListener;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;

/**
 * Demo eventListener.
 *
 * @author lix wang
 */
@Log4j2
public class DemoEventListener extends EventListener {
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
        log.info("Process to " + name + " consume " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
