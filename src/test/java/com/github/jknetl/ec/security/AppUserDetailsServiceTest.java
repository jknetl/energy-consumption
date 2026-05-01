package com.github.jknetl.ec.security;

import com.github.jknetl.ec.data.model.AppUser;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.data.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock AppUserRepository userRepository;
    @InjectMocks AppUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setEmail("user@test.com");
        user.setTenant(new Tenant(UUID.randomUUID(), "Tenant A", null));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        AppUserDetails details = (AppUserDetails) userDetailsService.loadUserByUsername("user@test.com");

        assertThat(details.getUsername()).isEqualTo("user@test.com");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_shouldThrow() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
