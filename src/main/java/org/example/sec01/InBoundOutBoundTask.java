package org.example.sec01;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class InBoundOutBoundTask {

    final static int NB_PLATFORM_THREAD = 10_000;
    final static int NB_VIRTAUL_THREAD = 50_000;

    public static void main(String[] args) throws InterruptedException {
        virtualThreadDemo();
    }

    /**
     * Create a simple thread with Thread Builder*
     */
    public static void platformThreadDemo() {

        IntStream.range(1, NB_PLATFORM_THREAD)
                .forEach(value -> new Thread(() -> Task.readFile(value))
                        .start());
    }

    /**
     * Create a daemon thread with Thread Builder*
     */
    public static void platformThreadDemo2() throws InterruptedException {
        var latch = new CountDownLatch(NB_PLATFORM_THREAD);
        IntStream.range(0, NB_PLATFORM_THREAD)
                .forEach(value -> Thread.ofPlatform().daemon()
                        .name("daemon")
                        .start(() -> {
                            Task.readFile(value);
                            latch.countDown();
                        }));
        latch.await();
    }

    /**
     * Create a virtual thread with Thread Builder
     * - virtual thread is a daemon thread by default
     */
    public static void virtualThreadDemo() throws InterruptedException {

        var latch = new CountDownLatch(NB_VIRTAUL_THREAD);
        IntStream.range(0, NB_VIRTAUL_THREAD)
                .forEach(value -> Thread.ofVirtual()
                        .name("virtual-")
                        .start(() -> {
                            Task.readFile(value);
                            latch.countDown();
                        }));
        latch.await();
    }

}
