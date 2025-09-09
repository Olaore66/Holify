package Holify.holify.repository;


import Holify.holify.entity.AudiobookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudiobookChapterRepository extends JpaRepository<AudiobookChapter, Long> {
    List<AudiobookChapter> findByAudiobookId(Long audiobookId);
}
