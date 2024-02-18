package com.deepak.registrationservice.repository;

import com.deepak.registrationservice.model.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {

    Flux<User> findAll();

    Flux<User> findAllBy(Pageable pageable);

    Mono<User> findById(Integer id);

    Mono<User> findByPhoneNumber(String phoneNumber);
}
