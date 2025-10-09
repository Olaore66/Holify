package Holify.holify.repository;

import Holify.holify.entity.UserArtistPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserArtistPreferenceRepository extends JpaRepository<UserArtistPreference, Long> {
    List<UserArtistPreference> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}