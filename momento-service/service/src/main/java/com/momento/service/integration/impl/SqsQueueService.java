package com.momento.service.integration.impl;

import com.momento.service.integration.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
@Profile("aws")
@RequiredArgsConstructor
public class SqsQueueService implements QueueService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Override
    public void enqueuePhoto(Long photoId) {
        LOGGER.info("Enqueuing photo ID: {} to SQS queue: {}", photoId, queueUrl);
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(photoId.toString())
                .build();

        sqsClient.sendMessage(sendMsgRequest);
        LOGGER.debug("Successfully enqueued photo ID: {} to SQS", photoId);
    }
}
