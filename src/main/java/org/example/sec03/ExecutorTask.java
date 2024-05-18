package org.example.sec03;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTask {

    private static final Logger log = LoggerFactory.getLogger(ExecutorTask.class);

    public static void main(String[] args) {

        //AutoClose
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.submit(ExecutorTask::demo);
            log.info("Task submitted");
        }
    }

    static void demo() {
        CommonUtils.sleep(Duration.ofSeconds(1));
        log.info("Task is finish");
    }
}
