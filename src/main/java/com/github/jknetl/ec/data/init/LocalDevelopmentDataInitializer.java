package com.github.jknetl.ec.data.init;

import com.github.jknetl.ec.data.model.*;
import com.github.jknetl.ec.data.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDevelopmentDataInitializer implements ApplicationRunner {

    private final TenantRepository tenantRepository;
    private final AppUserRepository userRepository;
    private final LocationRepository locationRepository;
    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.user-a-password}")
    private String userAPassword;

    @Value("${app.init.user-b-password}")
    private String userBPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Tenant tenantA = initTenant("Tenant A");
        Tenant tenantB = initTenant("Tenant B");

        initUser(tenantA, "user-a@example.com", userAPassword);
        initUser(tenantB, "user-b@example.com", userBPassword);

        List<Location> locations = initLocations(tenantA);
        List<Meter> meters = initMeters(tenantA, locations);
        initReadings(tenantA, meters);
    }

    private Tenant initTenant(String name) {
        return tenantRepository.findByName(name)
                .orElseGet(() -> {
                    Tenant t = new Tenant();
                    t.setName(name);
                    return tenantRepository.save(t);
                });
    }

    private void initUser(Tenant tenant, String email, String plainPassword) {
        if (userRepository.findByEmail(email).isPresent()) return;
        AppUser user = new AppUser();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(plainPassword));
        user.setDisplayName(email.split("@")[0]);
        userRepository.save(user);
    }

    private List<Location> initLocations(Tenant tenant) {
        Location prague = locationRepository
                .findByStreetAndStreetNumberAndCity("Main Street", 1, "Prague")
                .orElseGet(() -> {
                    Location loc = new Location();
                    loc.setTenant(tenant);
                    loc.setStreet("Main Street");
                    loc.setStreetNumber(1);
                    loc.setPostalCode(11000);
                    loc.setCity("Prague");
                    loc.setCountryCode("CZE");
                    return locationRepository.save(loc);
                });

        Location brno = locationRepository
                .findByStreetAndStreetNumberAndCity("Oak Avenue", 42, "Brno")
                .orElseGet(() -> {
                    Location loc = new Location();
                    loc.setTenant(tenant);
                    loc.setStreet("Oak Avenue");
                    loc.setStreetNumber(42);
                    loc.setPostalCode(60200);
                    loc.setCity("Brno");
                    loc.setCountryCode("CZE");
                    return locationRepository.save(loc);
                });

        return List.of(prague, brno);
    }

    private List<Meter> initMeters(Tenant tenant, List<Location> locations) {
        Location prague = locations.get(0);
        Location brno = locations.get(1);
        return List.of(
            findOrCreateMeter(tenant, prague, EnergyType.ELECTRICITY),
            findOrCreateMeter(tenant, prague, EnergyType.GAS),
            findOrCreateMeter(tenant, brno,   EnergyType.ELECTRICITY),
            findOrCreateMeter(tenant, brno,   EnergyType.GAS)
        );
    }

    private Meter findOrCreateMeter(Tenant tenant, Location location, EnergyType type) {
        return meterRepository.findByLocationAndType(location, type)
                .orElseGet(() -> {
                    Meter m = new Meter();
                    m.setTenant(tenant);
                    m.setLocation(location);
                    m.setType(type);
                    return meterRepository.save(m);
                });
    }

    private void initReadings(Tenant tenant, List<Meter> meters) {
        seedReadings(tenant, meters.get(0), EnergyUnit.KWH, new double[]{
            120.5, 135.2, 98.7, 142.0, 167.3, 88.4, 201.6, 155.9,
            110.2, 178.5, 93.1, 221.4, 145.8, 189.0
        });
        seedReadings(tenant, meters.get(1), EnergyUnit.CUBIC_METER, new double[]{
            45.3, 38.7, 52.1, 29.5, 61.8, 44.2, 33.6, 57.9, 41.0, 68.4, 36.2
        });
        seedReadings(tenant, meters.get(2), EnergyUnit.KWH, new double[]{
            95.2, 188.4, 147.6, 213.8, 102.5, 176.3, 231.9, 89.7, 165.4,
            198.2, 118.6, 243.1, 155.7, 87.3, 209.5, 134.8, 172.6, 250.0
        });
        seedReadings(tenant, meters.get(3), EnergyUnit.CUBIC_METER, new double[]{
            22.4, 54.8, 31.6, 67.3, 18.9, 43.7, 76.2, 28.5, 59.1, 35.8, 72.4, 15.3, 48.9
        });
    }

    private void seedReadings(Tenant tenant, Meter meter, EnergyUnit unit, double[] values) {
        if (!meterReadingRepository.findAllByMeterId(meter.getId()).isEmpty()) return;
        Instant start = Instant.parse("2026-05-05T11:40:00Z");
        for (int i = 0; i < values.length; i++) {
            MeterReading reading = new MeterReading();
            reading.setTenant(tenant);
            reading.setMeter(meter);
            reading.setUnit(unit);
            reading.setValue(BigDecimal.valueOf(values[i]));
            reading.setTakenAt(start.plus(i, ChronoUnit.DAYS));
            meterReadingRepository.save(reading);
        }
    }
}
