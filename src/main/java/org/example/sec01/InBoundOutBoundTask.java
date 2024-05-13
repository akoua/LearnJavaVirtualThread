package org.example.sec01;

import java.util.stream.IntStream;

public class InBoundOutBoundTask {
    public static void main(String[] args) {
        platformThreadDemo();
    }

    public static void platformThreadDemo() {

        IntStream.range(1, 50_001)
                .forEach(value -> new Thread(() -> Task.readFile(value))
                        .start());
    }


}
