
package Holify.holify.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_genre_preferences")
@Data
public class UserGenrePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String genre;
}