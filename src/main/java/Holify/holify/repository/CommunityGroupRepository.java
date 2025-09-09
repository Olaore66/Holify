package Holify.holify.repository;

import Holify.holify.entity.CommunityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityGroupRepository extends JpaRepository<CommunityGroup, Long> {}
