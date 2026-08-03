package com.momentoworker.data.repository;

import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.OrganizerAgreement;
import com.momentoworker.data.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerAgreementRepository extends JpaRepository<OrganizerAgreement, Long> {
    Optional<OrganizerAgreement> findByEvent(Event event);
    Optional<OrganizerAgreement> findByOrganizerAndEvent(UserAccount organizer, Event event);
}
