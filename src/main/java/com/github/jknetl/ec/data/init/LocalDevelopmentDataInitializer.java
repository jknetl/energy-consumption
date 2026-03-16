package com.github.jknetl.ec.data.init;

import com.github.jknetl.ec.data.model.EnergyType;
import com.github.jknetl.ec.data.model.EnergyUnit;
import com.github.jknetl.ec.data.model.Location;
import com.github.jknetl.ec.data.model.Meter;
import com.github.jknetl.ec.data.model.MeterReading;
import com.github.jknetl.ec.data.model.Tenant;
import com.github.jknetl.ec.data.repository.LocationRepository;
import com.github.jknetl.ec.data.repository.MeterReadingRepository;
import com.github.jknetl.ec.data.repository.MeterRepository;
import com.github.jknetl.ec.data.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDevelopmentDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final TenantRepository tenantRepository;
    private final LocationRepository locationRepository;
    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Tenant tenant = initTenant();
        List<Location> locations = initLocations(tenant);
        List<Meter> meters = initMeters(tenant, locations);
        initReadings(tenant, meters);
    }

    private Tenant initTenant() {
        jdbcTemplate.update(
            "INSERT INTO tenant (id, name) VALUES (?, ?) ON CONFLICT (id) DO NOTHING",
            Tenant.UNIMPLEMENTED_TENANT_ID, "UNIMPLEMENTED"
        );
        return tenantRepository.findById(Tenant.UNIMPLEMENTED_TENANT_ID).orElseThrow();
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

        Meter pragueElec = findOrCreateMeter(tenant, prague, EnergyType.ELECTRICITY);
        Meter pragueGas  = findOrCreateMeter(tenant, prague, EnergyType.GAS);
        Meter brnoElec   = findOrCreateMeter(tenant, brno,   EnergyType.ELECTRICITY);
        Meter brnoGas    = findOrCreateMeter(tenant, brno,   EnergyType.GAS);

        return List.of(pragueElec, pragueGas, brnoElec, brnoGas);
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
        // Prague ELECTRICITY — 14 readings (kWh)
        seedReadings(tenant, meters.get(0), EnergyUnit.KWH, new double[]{
            120.5, 135.2, 98.7, 142.0, 167.3, 88.4, 201.6, 155.9,
            110.2, 178.5, 93.1, 221.4, 145.8, 189.0
        });
        // Prague GAS — 11 readings (m³)
        seedReadings(tenant, meters.get(1), EnergyUnit.CUBIC_METER, new double[]{
            45.3, 38.7, 52.1, 29.5, 61.8, 44.2, 33.6, 57.9, 41.0, 68.4, 36.2
        });
        // Brno ELECTRICITY — 18 readings (kWh)
        seedReadings(tenant, meters.get(2), EnergyUnit.KWH, new double[]{
            95.2, 188.4, 147.6, 213.8, 102.5, 176.3, 231.9, 89.7, 165.4,
            198.2, 118.6, 243.1, 155.7, 87.3, 209.5, 134.8, 172.6, 250.0
        });
        // Brno GAS — 13 readings (m³)
        seedReadings(tenant, meters.get(3), EnergyUnit.CUBIC_METER, new double[]{
            22.4, 54.8, 31.6, 67.3, 18.9, 43.7, 76.2, 28.5, 59.1, 35.8, 72.4, 15.3, 48.9
        });
    }

    private void seedReadings(Tenant tenant, Meter meter, EnergyUnit unit, double[] values) {
        if (!meterReadingRepository.findAllByMeterId(meter.getId()).isEmpty()) {
            return;
        }
        for (double value : values) {
            MeterReading reading = new MeterReading();
            reading.setTenant(tenant);
            reading.setMeter(meter);
            reading.setUnit(unit);
            reading.setValue(BigDecimal.valueOf(value));
            meterReadingRepository.save(reading);
        }
    }
}
