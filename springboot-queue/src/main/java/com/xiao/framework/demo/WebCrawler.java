package com.xiao.framework.demo;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demo to build a web crawler.
 *
 * @author lix wang
 */
public abstract class WebCrawler {
    private volatile TrackingExecutor exec;
    private final Set<URL> urlsToCrawl = new HashSet<>();

    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl) {
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUnCrawled(exec.shutdownNow());
            if (exec.awaitTermination(30, TimeUnit.SECONDS)) {
                saveUnCrawled(exec.getCancelledTasks());
            }
        } finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUnCrawled(List<Runnable> runnables) {
        for (Runnable task : runnables) {
            urlsToCrawl.add(((CrawlTask) task).getPage());
        }
    }

    private void submitCrawlTask(URL url) {
        exec.execute(new CrawlTask(url));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return this.url;
        }
    }
}
