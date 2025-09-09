package Holify.holify.repository;

import Holify.holify.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, Long> {
    List<Podcast> findByCategory(String category);
    List<Podcast> findByHost(String host);
}
