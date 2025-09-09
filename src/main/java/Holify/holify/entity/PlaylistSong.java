package Holify.holify.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "playlist_songs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistSong {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name="playlist_id", nullable=false)
    private Playlist playlist;

    @ManyToOne @JoinColumn(name="track_id", nullable=false)
    private Track track;

    @Column(nullable=false) private Integer position;
}

