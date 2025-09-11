package Holify.holify.repository;

import Holify.holify.ENUMS.OtpPurpose;
import Holify.holify.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);

    Optional<OtpToken> findByEmailAndOtpAndPurpose(String email, String otp, OtpPurpose purpose);
}
