package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void hash_thenMatches_true() {
        String stored = hasher.hash("s3cret");
        assertThat(stored).contains(":");
        assertThat(hasher.matches("s3cret", stored)).isTrue();
    }

    @Test
    void matches_wrongPassword_false() {
        String stored = hasher.hash("s3cret");
        assertThat(hasher.matches("wrong", stored)).isFalse();
    }

    @Test
    void hash_usesRandomSalt_soTwoHashesDiffer() {
        assertThat(hasher.hash("same")).isNotEqualTo(hasher.hash("same"));
    }

    @Test
    void matches_nullStored_false() {
        assertThat(hasher.matches("x", null)).isFalse();
    }

    @Test
    void matches_malformedStored_false() {
        assertThat(hasher.matches("x", "no-colon-here")).isFalse();
    }
}
