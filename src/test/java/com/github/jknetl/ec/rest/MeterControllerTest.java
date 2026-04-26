package com.github.jknetl.ec.rest;

import tools.jackson.databind.ObjectMapper;
import com.github.jknetl.ec.data.model.EnergyType;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.rest.dto.MeterRequest;
import com.github.jknetl.ec.rest.dto.MeterResponse;
import com.github.jknetl.ec.rest.mapper.MeterMapper;
import com.github.jknetl.ec.service.MeterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

@WebMvcTest(MeterController.class)
class MeterControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private MeterService meterService;
    @MockitoBean private MeterMapper meterMapper;

    private static final String BASE_PATH = "/api/meters";

    private MeterRequest validRequest() {
        return new MeterRequest(EnergyType.ELECTRICITY, 1L);
    }

    @Test
    void getAll_whenMetersExist_shouldReturn200WithMeterList() throws Exception {
        MeterResponse response = new MeterResponse(1L, EnergyType.ELECTRICITY, 1L);
        when(meterService.findAll(any())).thenReturn(List.of());
        when(meterMapper.map(any(List.class))).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("ELECTRICITY"));
    }

    @Test
    void getAll_whenNoMeters_shouldReturn200WithEmptyList() throws Exception {
        when(meterService.findAll(any())).thenReturn(List.of());
        when(meterMapper.map(any(List.class))).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void get_whenMeterExists_shouldReturn200WithMeter() throws Exception {
        MeterResponse response = new MeterResponse(1L, EnergyType.GAS, 2L);
        when(meterService.findById(any(), eq(1L))).thenReturn(Optional.of(new Meter()));
        when(meterMapper.map(any(Meter.class))).thenReturn(response);

        mockMvc.perform(get(BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("GAS"))
                .andExpect(jsonPath("$.locationId").value(2));
    }

    @Test
    void get_whenMeterDoesNotExist_shouldReturn200WithNullBody() throws Exception {
        when(meterService.findById(any(), eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_PATH + "/99")).andExpect(status().isOk());
    }

    @Test
    void add_whenValidRequest_shouldReturn201() throws Exception {
        when(meterMapper.map(any(), any(), any())).thenReturn(new Meter());
        when(meterService.create(any(), any())).thenReturn(new Meter());

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());
        verify(meterService).create(any(), any());
    }

    @Test
    void add_whenTypeIsNull_shouldReturn400() throws Exception {
        MeterRequest invalidRequest = new MeterRequest(null, 1L);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_whenLocationIdIsNull_shouldReturn400() throws Exception {
        MeterRequest invalidRequest = new MeterRequest(EnergyType.ELECTRICITY, null);

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @EnumSource(EnergyType.class)
    void add_forAllEnergyTypes_shouldReturn201(EnergyType type) throws Exception {
        when(meterMapper.map(any(), any(), any())).thenReturn(new Meter());
        when(meterService.create(any(), any())).thenReturn(new Meter());

        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MeterRequest(type, 1L))))
                .andExpect(status().isCreated());
    }

    @Test
    void update_whenValidRequest_shouldReturn200() throws Exception {
        when(meterMapper.map(any(), any(), any())).thenReturn(new Meter());
        when(meterService.update(any(), any())).thenReturn(new Meter());

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
        verify(meterService).update(any(), any());
    }

    @Test
    void update_whenTypeIsNull_shouldReturn400() throws Exception {
        MeterRequest invalidRequest = new MeterRequest(null, 1L);

        mockMvc.perform(put(BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_whenCalled_shouldReturn200() throws Exception {
        // DELETE with no Content-Type is correct REST behavior.
        // Will return 415 if the endpoint still has consumes = APPLICATION_JSON_VALUE — that's the production bug.
        mockMvc.perform(delete(BASE_PATH + "/1")).andExpect(status().isOk());
        verify(meterService).deleteById(any(), eq(1L));
    }
}
