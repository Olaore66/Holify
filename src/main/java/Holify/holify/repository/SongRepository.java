package Holify.holify.repository;


import Holify.holify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByGenre(String genre);
    List<Song> findByArtist(String artist);
    List<Song> findByLanguage(String language);

    @Query("SELECT DISTINCT s.genre FROM Song s")
    List<String> findDistinctGenres();
    @Query("SELECT DISTINCT s.artist FROM Song s")
    List<String> findDistinctArtists();
}
