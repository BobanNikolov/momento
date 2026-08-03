package com.momentoworker.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "guest_search")
@Getter
@Setter
public class GuestSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "selfie_s3_key")
    private String selfieS3Key;

    @Column(name = "consent_accepted", nullable = false)
    private Boolean consentAccepted;

    @Column(name = "consent_policy_version")
    private String consentPolicyVersion;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    @Column(name = "result_count")
    private Integer resultCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "selfie_deleted_at")
    private LocalDateTime selfieDeletedAt;
}
