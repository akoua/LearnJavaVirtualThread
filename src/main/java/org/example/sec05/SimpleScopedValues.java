package org.example.sec05;

import org.example.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

public class SimpleScopedValues {
    static final Logger log = LoggerFactory.getLogger(SimpleScopedValues.class);
    static final ScopedValue<String> SESSION_TOKEN = ScopedValue.newInstance();

    public static void main(String[] args) {
        Thread.ofVirtual().name("1").start(() -> startRequest());
        Thread.ofVirtual().name("2").start(() -> startRequest());

        //Becaue virtual thread is a daemon thread
        CommonUtils.sleep(Duration.ofSeconds(1));
    }

    static void startRequest() {
        var token = authenticate();
        ScopedValue.runWhere(SESSION_TOKEN, token, () -> controller());
        // SESSION_TOKEN is not available here, because it's available only on the runWhere scope
    }

    private static String authenticate() {
        String token = String.valueOf(UUID.randomUUID());
        log.info("Auth token: {}", token);
        return token;
    }

    private static void controller() {
        log.info("Controller {}", SESSION_TOKEN.get());
        service();
    }

    private static void service() {
        log.info("Service {}", SESSION_TOKEN.get());
        ScopedValue.runWhere(SESSION_TOKEN, "Thread-" + Thread.currentThread().getName(), () -> externalService());
        Thread.ofVirtual().name("3").start(() -> externalServiceWithoutScopeValue());
    }

    private static void externalService() {
        // We lost scoped Value here, because we start another thread
        log.info("Call external service with session token: {}", SESSION_TOKEN.get());
    }

    private static void externalServiceWithoutScopeValue() {
        // We lost scoped Value here, because we start another thread
        log.info("Call external service with session token: {}", SESSION_TOKEN.get());
    }
}
