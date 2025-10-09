package Holify.holify.entity;

import Holify.holify.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_artist_preferences")
@Data
public class UserArtistPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String artist;
}