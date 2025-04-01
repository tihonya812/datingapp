package com.tihonya.datingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "liker_profile_id", nullable = false)
    private Profile liker; // Кто ставит лайк

    @ManyToOne
    @JoinColumn(name = "liked_profile_id", nullable = false)
    private Profile liked; // Кого лайкают

    private Instant createdAt = Instant.now(); // Когда лайк был поставлен
}



