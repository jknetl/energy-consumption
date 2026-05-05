package com.github.jknetl.ec.rest;

import tools.jackson.databind.ObjectMapper;
import com.github.jknetl.ec.data.model.EnergyUnit;
import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.rest.dto.MeterReadingRequest;
import com.github.jknetl.ec.rest.dto.MeterReadingResponse;
import com.github.jknetl.ec.data.repository.AppUserRepository;
import com.github.jknetl.ec.rest.mapper.MeterReadingMapper;
import com.github.jknetl.ec.security.AppUserDetails;
import com.github.jknetl.ec.security.JwtService;
import com.github.jknetl.ec.security.SecurityConfig;
import com.github.jknetl.ec.service.MeterReadingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.github.jknetl.ec.TestEntityFactory.createAppUser;
import static com.github.jknetl.ec.TestEntityFactory.createTenantA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterReadingController.class)
@Import(SecurityConfig.class)
class MeterReadingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private MeterReadingService meterReadingService;
    @MockitoBean private MeterReadingMapper meterReadingMapper;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private AppUserRepository appUserRepository;

    private static final String BASE_PATH = "/api/meters/1/readings";

    private static final Instant TAKEN_AT = Instant.parse("2024-01-15T10:00:00Z");

    private MeterReadingRequest validRequest() {
        return new MeterReadingRequest(new BigDecimal("100.00"), EnergyUnit.KWH, TAKEN_AT);
    }

    private AppUserDetails testUser() {
        return new AppUserDetails(createAppUser(createTenantA()));
    }

    @Test
    void getAll_whenReadingsExist_shouldReturn200WithReadingList() throws Exception {
        MeterReadingResponse response = new MeterReadingResponse(1L, new BigDecimal("100.00"), EnergyUnit.KWH, TAKEN_AT, 1L);
        when(meterReadingService.findAll(any(), eq(1L))).thenReturn(List.of());
        when(meterReadingMapper.map(any(List.class))).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_PATH).with(user(testUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].unit").value("KWH"));
    }

    @Test
    void getAll_whenNoReadings_shouldReturn200WithEmptyList() throws Exception {
        when(meterReadingService.findAll(any(), eq(1L))).thenReturn(List.of());
        when(meterReadingMapper.map(any(List.class))).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH).with(user(testUser())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAll_whenUnauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void get_whenReadingExists_shouldReturn200WithReading() throws Exception {
        MeterReadingResponse response = new MeterReadingResponse(1L, new BigDecimal("50.00"), EnergyUnit.CUBIC_METER, TAKEN_AT, 1L);
        when(meterReadingService.findById(any(), eq(1L))).thenReturn(Optional.of(new MeterReading()));
        when(meterReadingMapper.map(any(MeterReading.class))).thenReturn(response);

        mockMvc.perform(get(BASE_PATH + "/1").with(user(testUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unit").value("CUBIC_METER"));
    }

    @Test
    void get_whenReadingDoesNotExist_shouldReturn200WithNullBody() throws Exception {
        when(meterReadingService.findById(any(), eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_PATH + "/99").with(user(testUser()))).andExpect(status().isOk());
    }

    @Test
    void add_whenValidRequest_shouldReturn201() throws Exception {
        when(meterReadingMapper.map(any(), any(), any())).thenReturn(new MeterReading());
        when(meterReadingService.create(any(), eq(1L), any())).thenReturn(new MeterReading());

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());
        verify(meterReadingService).create(any(), eq(1L), any());
    }

    @Test
    void add_whenValueIsNull_shouldReturn400() throws Exception {
        MeterReadingRequest invalidRequest = new MeterReadingRequest(null, EnergyUnit.KWH, TAKEN_AT);

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenValueIsNegative_shouldReturn400() throws Exception {
        MeterReadingRequest invalidRequest = new MeterReadingRequest(new BigDecimal("-1"), EnergyUnit.KWH, TAKEN_AT);

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenUnitIsNull_shouldReturn400() throws Exception {
        MeterReadingRequest invalidRequest = new MeterReadingRequest(new BigDecimal("100.00"), null, TAKEN_AT);

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @EnumSource(EnergyUnit.class)
    void add_forAllEnergyUnits_shouldReturn201(EnergyUnit unit) throws Exception {
        when(meterReadingMapper.map(any(), any(), any())).thenReturn(new MeterReading());
        when(meterReadingService.create(any(), eq(1L), any())).thenReturn(new MeterReading());

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MeterReadingRequest(new BigDecimal("100.00"), unit, TAKEN_AT))))
                .andExpect(status().isCreated());
    }

    @Test
    void update_whenValidRequest_shouldReturn200() throws Exception {
        when(meterReadingMapper.map(any(), any(), any())).thenReturn(new MeterReading());
        when(meterReadingService.update(any(), eq(1L), any())).thenReturn(new MeterReading());

        mockMvc.perform(put(BASE_PATH + "/1")
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
        verify(meterReadingService).update(any(), eq(1L), any());
    }

    @Test
    void update_whenValueIsNegative_shouldReturn400() throws Exception {
        MeterReadingRequest invalidRequest = new MeterReadingRequest(new BigDecimal("-1"), EnergyUnit.KWH, TAKEN_AT);

        mockMvc.perform(put(BASE_PATH + "/1")
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenTakenAtIsNull_shouldReturn400() throws Exception {
        MeterReadingRequest invalidRequest = new MeterReadingRequest(new BigDecimal("100.00"), EnergyUnit.KWH, null);

        mockMvc.perform(post(BASE_PATH)
                        .with(user(testUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_whenCalled_shouldReturn200() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/1").with(user(testUser()))).andExpect(status().isOk());
        verify(meterReadingService).deleteById(any(), eq(1L));
    }
}
