package com.momentoworker.data.repository;

import com.momentoworker.data.model.Download;
import com.momentoworker.data.model.Event;
import com.momentoworker.data.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {
    List<Download> findByEvent(Event event);
    List<Download> findByPhoto(Photo photo);
    void deleteByEvent(Event event);
}
