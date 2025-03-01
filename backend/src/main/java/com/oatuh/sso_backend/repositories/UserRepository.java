package com.oatuh.sso_backend.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oatuh.sso_backend.entitites.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User>findByEmail(String email);

}
