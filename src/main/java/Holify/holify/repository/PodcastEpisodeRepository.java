package Holify.holify.repository;

import Holify.holify.entity.PodcastEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PodcastEpisodeRepository extends JpaRepository<PodcastEpisode, Long> {
    List<PodcastEpisode> findByPodcastId(Long podcastId);
}
