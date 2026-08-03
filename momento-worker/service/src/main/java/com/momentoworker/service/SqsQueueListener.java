package com.momentoworker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Component
@Profile("aws")
@RequiredArgsConstructor
@Slf4j
public class SqsQueueListener {

    private final SqsClient sqsClient;
    private final PhotoProcessingWorker photoProcessingWorker;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 1000)
    public void listen() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .build();

        try {
            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            for (Message message : response.messages()) {
                try {
                    Long photoId = Long.parseLong(message.body());
                    LOGGER.info("SqsQueueListener: received photoId {}", photoId);
                    
                    try {
                        photoProcessingWorker.processPhoto(photoId);
                    } catch (Exception e) {
                        LOGGER.error("Error processing photo {}: {}", photoId, e.getMessage());
                        if (isPermanentFailure(e)) {
                            LOGGER.warn("Permanent failure detected for photo {}, deleting message", photoId);
                        } else {
                            // Rethrow to skip deletion, allowing SQS retry
                            throw e;
                        }
                    }

                    sqsClient.deleteMessage(DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                } catch (Exception e) {
                    LOGGER.error("Failed to process message: {}", message.body(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to receive messages from SQS", e);
        }
    }

    private boolean isPermanentFailure(Exception e) {
        Throwable t = e;
        while (t != null) {
            String message = t.getMessage();
            if (message != null && (message.contains("Photo not found") || message.contains("NoSuchKey"))) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
