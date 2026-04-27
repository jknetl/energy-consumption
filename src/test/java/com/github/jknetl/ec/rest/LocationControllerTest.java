package com.github.jknetl.ec.rest;

import tools.jackson.databind.ObjectMapper;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.rest.dto.LocationRequest;
import com.github.jknetl.ec.rest.dto.LocationResponse;
import com.github.jknetl.ec.rest.mapper.LocationMapper;
import com.github.jknetl.ec.service.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private LocationService locationService;
    @MockitoBean private LocationMapper locationMapper;

    private static final String BASE_PATH = "/api/locations";

    private LocationRequest validRequest() {
        return new LocationRequest("Main Street", 1, 10000, "Prague", "CZE");
    }

    @Test
    void getAll_whenLocationsExist_shouldReturn200WithLocationList() throws Exception {
        LocationResponse response = new LocationResponse(1L, "Main Street", 1, 10000, "Prague", "CZE");
        when(locationService.findAll(any())).thenReturn(List.of());
        when(locationMapper.map(any(List.class))).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].city").value("Prague"));
    }

    @Test
    void getAll_whenNoLocations_shouldReturn200WithEmptyList() throws Exception {
        when(locationService.findAll(any())).thenReturn(List.of());
        when(locationMapper.map(any(List.class))).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void get_whenLocationExists_shouldReturn200WithLocation() throws Exception {
        LocationResponse response = new LocationResponse(1L, "Main Street", 1, 10000, "Prague", "CZE");
        when(locationService.findById(any(), eq(1L))).thenReturn(Optional.of(new Location()));
        when(locationMapper.map(any(Location.class))).thenReturn(response);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.countryCode").value("CZE"));
    }

    @Test
    void get_whenLocationDoesNotExist_shouldReturn200WithNullBody() throws Exception {
        when(locationService.findById(any(), eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_PATH + "/99"))
                .andExpect(status().isOk());
    }

    @Test
    void add_whenValidRequest_shouldReturn201() throws Exception {
        when(locationMapper.map(any(), any(), any())).thenReturn(new Location());
        when(locationService.create(any(), any())).thenReturn(new Location());

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());
        verify(locationService).create(any(), any());
    }

    @Test
    void add_whenStreetIsNull_shouldReturn400() throws Exception {
        LocationRequest invalidRequest = new LocationRequest(null, 1, 10000, "Prague", "CZE");

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenCityIsNull_shouldReturn400() throws Exception {
        LocationRequest invalidRequest = new LocationRequest("Main Street", 1, 10000, null, "CZE");

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "CZ", "CZEE"})
    void add_whenCountryCodeHasInvalidLength_shouldReturn400(String countryCode) throws Exception {
        LocationRequest invalidRequest = new LocationRequest("Main Street", 1, 10000, "Prague", countryCode);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_whenValidRequest_shouldReturn200() throws Exception {
        when(locationMapper.map(any(), any(), any())).thenReturn(new Location());
        when(locationService.update(any(), any())).thenReturn(new Location());

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
        verify(locationService).update(any(), any());
    }

    @Test
    void update_whenInvalidRequest_shouldReturn400() throws Exception {
        LocationRequest invalidRequest = new LocationRequest(null, 1, 10000, "Prague", "CZE");

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_whenCalled_shouldReturn200() throws Exception {
        // DELETE with no Content-Type is correct REST behavior.
        // Will return 415 if the endpoint still has consumes = APPLICATION_JSON_VALUE — that's the production bug.
        mockMvc.perform(delete(BASE_PATH + "/1"))
                .andExpect(status().isOk());
        verify(locationService).deleteById(any(), eq(1L));
    }
}
