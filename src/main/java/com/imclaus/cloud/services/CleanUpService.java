package com.imclaus.cloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CleanUpService {
    private final DatabaseClient client;

    @Autowired
    public CleanUpService(DatabaseClient client) {
        this.client = client;
    }

    @Scheduled( cron="0 0 0 * * *" )
    public void clear() {
        authClear().block();
        System.out.println("CleanUp Success!");
    }

    private Mono<Void> authClear() {
        return client.sql("DELETE FROM auth WHERE updated::date < CURRENT_TIMESTAMP")
                .map((row, rowMetadata) -> client.sql("REINDEX TABLE auth"))
                .all().then();
    }
}
