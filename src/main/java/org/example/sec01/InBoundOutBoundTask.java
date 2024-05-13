package org.example.sec01;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class InBoundOutBoundTask {

    final static int NB_TASK = 10;

    public static void main(String[] args) throws InterruptedException {
        platformThreadDemo2();
    }

    public static void platformThreadDemo() {

        IntStream.range(1, NB_TASK)
                .forEach(value -> new Thread(() -> Task.readFile(value))
                        .start());
    }

    public static void platformThreadDemo2() throws InterruptedException {
        var latch = new CountDownLatch(NB_TASK);
        IntStream.range(0, NB_TASK)
                .forEach(value -> Thread.ofPlatform().daemon()
                        .name("daemon")
                        .start(() -> {
                            Task.readFile(value);
                            latch.countDown();
                        }));
        latch.await();
    }

}
