package com.tihonya.datingapp.repository;

import com.tihonya.datingapp.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}

