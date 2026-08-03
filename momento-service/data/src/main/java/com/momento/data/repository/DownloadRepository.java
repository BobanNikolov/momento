package com.momento.data.repository;

import com.momento.data.model.Download;
import com.momento.data.model.Event;
import com.momento.data.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {
    List<Download> findByEvent(Event event);
    List<Download> findByPhoto(Photo photo);
}
