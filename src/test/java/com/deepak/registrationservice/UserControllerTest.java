package com.deepak.registrationservice;

import com.deepak.registrationservice.controller.UserController;
import com.deepak.registrationservice.model.user.User;
import com.deepak.registrationservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Test
    public void getUserById_validId() {
        // Arrange
        final Integer expectedId = 1;
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("John");
        expectedUser.setPhoneNumber("9876543210");
        expectedUser.setEmail("abc@xyz.com");
        expectedUser.setBirthdate(LocalDate.now());

        when(userRepository.findById(expectedId)).thenReturn(Mono.just(expectedUser));

        // Act
        var result = userController.getUserById(expectedId).block();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedUser.getId());
    }

}
