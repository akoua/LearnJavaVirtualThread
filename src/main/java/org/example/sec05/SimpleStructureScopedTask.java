package org.example.sec05;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SimpleStructureScopedTask {

    static final Logger log = LoggerFactory.getLogger(SimpleStructureScopedTask.class);

    record Airplane(String companyName, int price) {
    }

    static ScopedValue<String> SESSION_TOKEN = ScopedValue.newInstance();
    static final Random random = new Random();


    public static void main(String[] args) {
        ScopedValue.runWhere(SESSION_TOKEN, UUID.randomUUID().toString(), SimpleStructureScopedTask::task);
    }

    static void task() {
        try (var taskScoped = new StructuredTaskScope.ShutdownOnFailure()) {
            log.info(": Task SESSION_TOKEN value: {}", SESSION_TOKEN.get());
            var subTask1 = taskScoped.fork(() -> getAirCi());
            var subTask2 = taskScoped.fork(() -> getAirWorld());

            taskScoped.join();
            log.info(": Main after join process");

            Stream.of(subTask1.get(), subTask2.get())
                    .min(Comparator.comparingInt(st -> st.price))
                    .ifPresent(airplane -> log.info("The cheapest company is: {}", airplane));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Airplane getAirCi() {
        log.info("::SubTask AirCi SESSION_TOKEN value: {}", SESSION_TOKEN.get());
        AtomicInteger randomPrice = new AtomicInteger();
        Thread.ofVirtual().name("AirCi").start(() -> {
            log.info("::: SubSubTask AirCi SESSION_TOKEN value: {}", SESSION_TOKEN.get());
            randomPrice.set(random.nextInt(100));
            log.info("::: AirCi random price={}", randomPrice);
            CommonUtils.sleep("AirCi task", Duration.ofSeconds(1));
        });

        return new Airplane("AirCi", randomPrice.get());
    }

    static Airplane getAirWorld() {
        log.info("::SubTask AirWorld SESSION_TOKEN value: {}", SESSION_TOKEN.get());
        AtomicInteger randomPrice = new AtomicInteger();
        Thread.ofVirtual().name("AirCi").start(() -> {
            //SESSION_TOKEN is not available here
            log.info("::: SubSubTask AirWorld SESSION_TOKEN value: {}", SESSION_TOKEN.get());
            randomPrice.set(random.nextInt(100));
            log.info("::: AirWorld random price={}", randomPrice);
            CommonUtils.sleep("AirWorld task", Duration.ofSeconds(2));
        });

        return new Airplane("AirWorld", randomPrice.get());
    }
}
