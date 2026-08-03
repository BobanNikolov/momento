package com.momentoworker.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;

@Component
public class QueueHealthIndicator implements HealthIndicator {

    private final SqsClient sqsClient;

    public QueueHealthIndicator(@Autowired(required = false) SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public Health health() {
        if (sqsClient == null) {
            return Health.up().withDetail("queue", "Local (Simulation)").build();
        }
        try {
            sqsClient.listQueues(ListQueuesRequest.builder().maxResults(1).build());
            return Health.up().withDetail("queue", "AWS SQS").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("queue", "AWS SQS").build();
        }
    }
}
