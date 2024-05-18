package org.example.sec03;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.SheetCollate;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

        scheduledTask(100_000);
    }

    static void scheduledTask(int numberOfTask) {

        //we use a platform thread like a scheduler
        var scheduler = Executors.newScheduledThreadPool(1);
        //we use a platform thread like a scheduler
        var factory = Thread.ofVirtual().name("vi", 1).factory();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);
//        ExecutorService executor = Executors.newCachedThreadPool();

        try (scheduler; executor) {
            AtomicInteger batchNumber = new AtomicInteger(1);
            long scheduleBeginningTime = System.currentTimeMillis();

            scheduler.scheduleAtFixedRate(() -> {
                log.info("::::::: NEW BATCH {} at {}ms from beginning ::::", batchNumber.get(), (System.currentTimeMillis() - scheduleBeginningTime));
                long beginningTime = System.currentTimeMillis();
                List<Future<?>> listOfTaskFuture = new ArrayList<>();

                try {
                    BufferedWriter productFile = new BufferedWriter(new FileWriter(String.format("filename%s.txt", batchNumber.get())));
                    IntStream.range(0, numberOfTask)
                            .forEach(value -> {
                                try {
                                    Future<?> submitTask = executor.submit(() -> printProductInfo(value, productFile));
                                    listOfTaskFuture.add(submitTask);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
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
                        productFile.write("All time: " + (System.currentTimeMillis() - beginningTime) + "ms");
                        productFile.close();
                        log.info("::::Duration of batch {} treatment: {}ms of {} items ", batchNumber.getAndIncrement(),
                                (System.currentTimeMillis() - beginningTime), listOfTaskFuture.size());
                    }
                } catch (Exception e) {
                    //for debug only, nether for production
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.SECONDS);
            CommonUtils.sleep(Duration.ofSeconds(11));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printProductInfo(int id, BufferedWriter file) {
        try {
//            log.info("Beginning: {} => {}", id, "Product " + id);
            file.write("Product " + id + "\n");
//            log.info("Ending: {} => {}", id, "Product " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
