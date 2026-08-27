package ru.merezh.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.merezh.authservice.entity.UserAuth;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth> getUserAuthsById(long id);
    boolean existsUserAuthsById(long id);
    void deleteById(long id);
}
