package org.example.sec05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SimpleThreadLocal {
    static final Logger log = LoggerFactory.getLogger(SimpleThreadLocal.class);
    static final ThreadLocal<String> SESSION_TOKEN = new InheritableThreadLocal<>();

    public static void main(String[] args) {
        Thread.ofPlatform().start(() -> startRequest());
        Thread.ofPlatform().start(() -> startRequest());
    }

    static void startRequest() {
        authenticate();
        controller();
    }

    private static void authenticate() {
        String token = String.valueOf(UUID.randomUUID());
        log.info("Auth token: {}", token);
        SESSION_TOKEN.set(token);
    }

    private static void controller() {
        log.info("Controller {}", SESSION_TOKEN.get());
        service();
    }

    private static void service() {
        log.info("Service {}", SESSION_TOKEN.get());
        // We lost a threadLocal context here, so in externalService it's will be null
        Thread.ofPlatform().start(() -> externalService());
    }

    private static void externalService() {
        log.info("Call external service with session token: {}", SESSION_TOKEN.get());
    }
}
