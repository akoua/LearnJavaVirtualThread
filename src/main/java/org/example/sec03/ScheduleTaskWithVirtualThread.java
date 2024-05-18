package org.example.sec03;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.SheetCollate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ScheduleTaskWithVirtualThread {

    static final Logger log = LoggerFactory.getLogger(SheetCollate.class);

    public static void main(String[] args) {

        scheduledTask(1_000);
    }

    static void scheduledTask(int numberOfTask) {

        //we use a platform thread like a scheduler
        var scheduler = Executors.newScheduledThreadPool(1);
        //we use a platform thread like a scheduler
        var factory = Thread.ofVirtual().name("vi", 1).factory();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);
//        ExecutorService executor = Executors.newCachedThreadPool();

        try (scheduler; executor) {
            AtomicInteger productId = new AtomicInteger(1);
            AtomicInteger batchNumber = new AtomicInteger(1);

            scheduler.scheduleAtFixedRate(() -> {
                long beginningTime = System.currentTimeMillis();
                List<Future<?>> listOfTaskFuture = new ArrayList<>();

                IntStream.range(0, numberOfTask)
                        .forEach(value -> {
                            Future<?> submitTask = executor.submit(() -> printProductInfo(productId.getAndIncrement()));
                            listOfTaskFuture.add(submitTask);
                        });
                boolean taskState = listOfTaskFuture.stream()
                        .allMatch(taskFuture -> {
                            try {
                                taskFuture.get();
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        });
                if (taskState) {
                    log.info("::::Duration of batch {} treatment: {}ms", batchNumber.getAndIncrement(),
                            (System.currentTimeMillis() - beginningTime));
                }

            }, 0, 5, TimeUnit.SECONDS);
            CommonUtils.sleep(Duration.ofSeconds(30));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printProductInfo(int id) {
        try {
            log.info("Beginning: {} => {}", id, "Product " + id);
            Thread.sleep(Duration.ofSeconds(2));
            log.info("Ending: {} => {}", id, "Product " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
