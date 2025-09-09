package Holify.holify.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "podcast_episodes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PodcastEpisode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "podcast_id", nullable = false)
    private Podcast podcast;
}