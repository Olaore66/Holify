package Holify.holify.repository;


import Holify.holify.entity.Audiobook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudiobookRepository extends JpaRepository<Audiobook, Long> {
    List<Audiobook> findByAuthor(String author);
    List<Audiobook> findByNarrator(String narrator);
}
