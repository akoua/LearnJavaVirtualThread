package org.example.sec03;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ExecutorTypes {
    private static final Logger log = LoggerFactory.getLogger(ExecutorTypes.class);

    public static void main(String[] args) {
        long beginningTime = System.currentTimeMillis();
        virtualThreadExecutor();
        log.info(" --- Total executing time: {} ---", (System.currentTimeMillis() - beginningTime));
        beginningTime = System.currentTimeMillis();
        dynamicThreadExecutor();
        log.info(" --- Total executing time: {} ---", (System.currentTimeMillis() - beginningTime));
    }

    static void dynamicThreadExecutor() {
        prepareTask(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1), 100);
    }

    static void virtualThreadExecutor() {
        prepareTask(Executors.newVirtualThreadPerTaskExecutor(), 100);
    }

    static void prepareTask(ExecutorService executorService, int numberOfTask) {
        try (executorService) {
            IntStream.range(0, numberOfTask)
                    .forEach(value -> {
                        executorService.execute(() -> ioTask(value));
                        log.info("Task {} was submitted", value);
                    });
        }
    }

    static void ioTask(int task) {
        log.info("Task {} is starting", task);
        CommonUtils.sleep(Duration.ofSeconds(1));
        log.info("Task {} is ending", task);
    }
}
