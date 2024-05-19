package org.example.sec04;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SimpleCompletableFuture {

    static final Logger log = LoggerFactory.getLogger(SimpleCompletableFuture.class);

    public static void main(String[] args) {
        log.info("Beginning main");
//        CompletableFuture<String> cfResult = fastTask();
        CompletableFuture<String> cfResult = slowTask();
        cfResult.thenAccept(s -> log.info("Value={}", s));
        log.info("Ending main");
        CommonUtils.sleep(Duration.ofSeconds(2));
    }

    static CompletableFuture<String> fastTask() {
        log.info("Beginning task");
        CompletableFuture<String> cf = new CompletableFuture<>();
        cf.complete("Hi");
        log.info("Ending task");
        return cf;
    }

    static CompletableFuture<String> slowTask() {
        log.info("Beginning task");
        CompletableFuture<String> cf = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            CommonUtils.sleep(Duration.ofSeconds(1));
            cf.complete("Hi");
        });
        log.info("Ending task");
        return cf;
    }
}
