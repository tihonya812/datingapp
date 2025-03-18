package com.tihonya.datingapp.repository;

import com.tihonya.datingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

