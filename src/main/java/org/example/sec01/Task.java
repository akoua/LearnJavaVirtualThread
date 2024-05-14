package org.example.sec01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Task {

    final static Logger log = LoggerFactory.getLogger(Task.class);

    public static void readFile(int value) {
        try {
            log.info("Beginning task {}, Thread info {}", value, Thread.currentThread());
            Thread.sleep(Duration.ofSeconds(1));
            log.info("Ending task {}, Thread info {}", value, Thread.currentThread());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
