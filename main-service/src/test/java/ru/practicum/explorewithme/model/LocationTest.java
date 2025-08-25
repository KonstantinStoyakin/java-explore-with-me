package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    void testLocation() {
        Location location = new Location();
        location.setLat(10.5f);
        location.setLon(20.5f);

        assertThat(location.getLat()).isEqualTo(10.5f);
        assertThat(location.getLon()).isEqualTo(20.5f);

        Location built = Location.builder()
                .lat(1.1f)
                .lon(2.2f)
                .build();

        assertThat(built.getLat()).isEqualTo(1.1f);
        assertThat(built.getLon()).isEqualTo(2.2f);
    }
}
