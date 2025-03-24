package com.tihonya.datingapp.repository;

import com.tihonya.datingapp.model.Photo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByProfileId(Long profileId);
}
