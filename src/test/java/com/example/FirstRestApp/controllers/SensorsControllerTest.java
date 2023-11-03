package com.example.FirstRestApp.controllers;

import com.example.FirstRestApp.dto.SensorDTO;
import com.example.FirstRestApp.models.Sensor;
import com.example.FirstRestApp.sevices.SensorsService;
import com.example.FirstRestApp.validators.UniqueSensorValidator;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
@SpringBootTest
@Import(UniqueSensorValidator.class)
@TestMethodOrder(MethodOrderer.Random.class)
class SensorsControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private SensorsService sensorsService;

    @MockBean
    private UniqueSensorValidator validator;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDTO testSensorDTO;

    @Autowired
    public SensorsControllerTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void setUp() {
        testSensorDTO = new SensorDTO();
    }

    @Test
    public void createEmptySensorTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message", containsString("Имя сенсора не должно быть пустым!")));

        verify(validator, times(1)).validate(any(Sensor.class), any(BindingResult.class));
    }

    @Test
    public void createSensorWithNotValidName() throws Exception {
        testSensorDTO.setName("s");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Имя сенсора должно содержать от 3 до 30 символов")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        testSensorDTO.setName("sensor".repeat(6));

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Имя сенсора должно содержать от 3 до 30 символов")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        verify(validator, times(2)).validate(any(Sensor.class), any(BindingResult.class));
    }

    @Test
    public void createValidSensorTest() throws Exception {
        testSensorDTO.setName("A valid sensor");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        verify(validator, times(1)).validate(any(Sensor.class), any(BindingResult.class));
        verify(sensorsService, times(1)).save(any(Sensor.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(validator, sensorsService);
    }
}
