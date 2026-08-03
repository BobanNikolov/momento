package com.momento.data.repository;

import com.momento.data.model.Event;
import com.momento.data.model.OrganizerAgreement;
import com.momento.data.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerAgreementRepository extends JpaRepository<OrganizerAgreement, Long> {
    Optional<OrganizerAgreement> findByEvent(Event event);
    Optional<OrganizerAgreement> findByOrganizerAndEvent(UserAccount organizer, Event event);
}
