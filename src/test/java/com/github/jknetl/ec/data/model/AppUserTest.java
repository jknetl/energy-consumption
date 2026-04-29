package com.github.jknetl.ec.data.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserTest {

    @Test
    void createdAt_whenNotSet_shouldBePopulatedOnPrePersist() {
        AppUser user = new AppUser();
        user.prePersist();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void isActive_whenCreatedWithDefaults_shouldBeTrue() {
        AppUser user = new AppUser();
        assertThat(user.isActive()).isTrue();
    }
}
