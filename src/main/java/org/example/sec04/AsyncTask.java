package org.example.sec04;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class AsyncTask {
    static final Logger log = LoggerFactory.getLogger(SimpleCompletableFuture.class);

    public static void main(String[] args) {
        long beginningTime = System.currentTimeMillis();
        log.info("Beginning main");
//        runTask();
        runAsyncTask();
        log.info("Ending main after {}ms", (System.currentTimeMillis() - beginningTime));
        CommonUtils.sleep(Duration.ofSeconds(5));
    }

    /**
     * Even if you return void, this code is synchronous
     */
    static void runTask() {
        log.info("Beginning task");
        CommonUtils.sleep(Duration.ofSeconds(4));
        log.info("Ending task");
    }

    static void runAsyncTask() {
        log.info("Beginning task");
        CompletableFuture.runAsync(() -> {
            CommonUtils.sleep(Duration.ofSeconds(4));
            log.info("Async task is complete");
        }, Executors.newVirtualThreadPerTaskExecutor());
        log.info("Ending task");
    }


}
