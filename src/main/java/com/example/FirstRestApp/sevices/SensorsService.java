package com.example.FirstRestApp.sevices;

import com.example.FirstRestApp.exception.SensorNotFoundException;
import com.example.FirstRestApp.models.Sensor;
import com.example.FirstRestApp.reprositories.SensorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SensorsService {
    private final SensorsRepository sensorsRepository;

    @Autowired
    public SensorsService(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    @Transactional
    public void save(Sensor sensor) {
        sensorsRepository.save(sensor);
    }

    public Optional<Sensor> findOneByName(String name) {
        return sensorsRepository.findByName(name);
    }

    public Sensor findOneByNameOrElseThrowException(String name) {
        return sensorsRepository.findByName(name).orElseThrow(SensorNotFoundException::new);
    }
}
