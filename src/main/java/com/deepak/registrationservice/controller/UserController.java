package com.deepak.registrationservice.controller;

import com.deepak.registrationservice.exception.ErrorDetails;
import com.deepak.registrationservice.exception.UserNotFoundException;
import com.deepak.registrationservice.model.user.User;
import com.deepak.registrationservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@Tag(name = "User", description = "Handles CRUD operations for User registration")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("/users")
    @Operation(summary = "Retrieve all users", description = "Retrieve all users")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Users Retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))), @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Flux<User> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Retrieving all users");
        return userRepository.findAllBy(PageRequest.of(page, size)).doOnError(error -> LOGGER.error("Error retrieving users: {}", error.getMessage()));

    }

    @GetMapping("/user/by/id/{id}")
    @Operation(summary = "Retrieve a user by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User retrieved by id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))), @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<User> getUserById(@PathVariable Integer id) {
        LOGGER.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id).map(user -> {
            LOGGER.info("Retrieved user with ID: {}", user);
            return user;
        }).switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + id)));
    }

    @GetMapping("/user/by/phonenumber/{phoneNumber}")
    @Operation(summary = "Retrieve a user by phone number")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User retrieved by phone number", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))), @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        LOGGER.info("Attempting to retrieve user with phoneNumber: {}", phoneNumber);
        return userRepository.findByPhoneNumber(phoneNumber).flatMap(user -> {
            LOGGER.info("Retrieved user with phoneNumber: {}", phoneNumber);
            return Mono.just(user);
        }).switchIfEmpty(Mono.error(new UserNotFoundException("User not found with phoneNumber: " + phoneNumber)));
    }


    @PostMapping("/user")
    @Operation(summary = "Create a new User")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "User created"), @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))), @ApiResponse(responseCode = "409", description = "User already exists")})
    public Mono<User> createUser(@Valid @RequestBody User user) {
        LOGGER.info("Creating user: {}", user);
        return userRepository.save(user).doOnSuccess(createdUser -> LOGGER.info("User Id : {} has been created", createdUser.getId())).onErrorResume(e -> {
            if (e instanceof MethodArgumentNotValidException) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input", e);
            }
            return Mono.error(e);
        });
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Update user information")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "User information updated"), @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),})
    public Mono<User> updateUser(@PathVariable Integer id,@Valid @RequestBody User user) {
        LOGGER.info("Updating user with ID: {}", id);
        return userRepository.findById(id).flatMap(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setBirthdate(user.getBirthdate());
            return userRepository.save(existingUser);
        }).doOnSuccess(updatedUser -> LOGGER.info("User with ID {} has been updated", updatedUser.getId())).switchIfEmpty(Mono.error(new UserNotFoundException("User not found with ID: " + id)));

    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "delete user information by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "delete user information by id"), @ApiResponse(responseCode = "404", description = "User does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))})
    public Mono<Void> deleteUserById(@PathVariable Integer id) {
        LOGGER.info("Deleting user with ID: {}", id);
        return userRepository.findById(id).flatMap(user -> userRepository.delete(user).doOnSuccess(deletedUser -> LOGGER.info("Deleted user with ID: {}", id)).doOnError(error -> LOGGER.error("Error deleting user with ID {}: {}", id, error.getMessage()))).switchIfEmpty(Mono.fromRunnable(() -> LOGGER.warn("User with ID {} does not exist", id)));
    }


}
