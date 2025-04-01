package com.tihonya.datingapp.repository;

import com.tihonya.datingapp.model.Like;
import com.tihonya.datingapp.model.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("""
            SELECT l FROM Like l
            WHERE l.liker = :profile AND l.liked = :likedProfile
            """)
    Optional<Like> findLike(@Param("profile") Profile profile, @Param("likedProfile") Profile likedProfile);

    @Query("""
            SELECT l1.liked FROM Like l1
            JOIN Like l2 ON l1.liked = l2.liker AND l1.liker = l2.liked
            WHERE l1.liker = :profile
            AND l1.liked.age BETWEEN :minAge AND :maxAge
            """)
    List<Profile> findMatchesByAge(
            @Param("profile") Profile profile,
            @Param("minAge") int minAge,
            @Param("maxAge") int maxAge
    );
}