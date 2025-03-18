package com.tihonya.datingapp.repository;

import com.tihonya.datingapp.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
}
