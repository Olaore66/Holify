package Holify.holify.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@Table(name = "tracks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=200) private String title;
    @Column(nullable=false, length=150) private String artist; // simple string for MVP
    @Column(length=150) private String album;
    private Integer durationSec;
    @Column(nullable=false) private String fileUrl; // s3/cdn URL or objectKey
    @Column(length=100) private String genre;
    @Column(nullable=false) private Instant uploadedAt = Instant.now();
}

