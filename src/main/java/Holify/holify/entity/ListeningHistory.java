package Holify.holify.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "listening_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ListeningHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "played_at")
    private LocalDateTime playedAt = LocalDateTime.now();

    @Column(name = "seconds_played")
    private Integer secondsPlayed;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne @JoinColumn(name = "podcast_episode_id")
    private PodcastEpisode podcastEpisode;

    @ManyToOne @JoinColumn(name = "audiobook_chapter_id")
    private AudiobookChapter audiobookChapter;
}