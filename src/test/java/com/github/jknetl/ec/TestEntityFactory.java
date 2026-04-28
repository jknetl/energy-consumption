package com.github.jknetl.ec;

import com.github.jknetl.ec.data.model.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class TestEntityFactory {

    public static final UUID TENANT_A_ID = UUID.fromString("11111111-0000-0000-0000-000000000001");
    public static final UUID TENANT_B_ID = UUID.fromString("22222222-0000-0000-0000-000000000002");

    public static Tenant createTenantA() {
        return new Tenant(TENANT_A_ID, "Tenant A");
    }

    public static Tenant createTenantB() {
        return new Tenant(TENANT_B_ID, "Tenant B");
    }

    public static Location createLocation(Tenant tenant) {
        Location l = new Location();
        l.setTenant(tenant);
        l.setStreet("Main Street");
        l.setStreetNumber(1);
        l.setPostalCode(10000);
        l.setCity("Prague");
        l.setCountryCode("CZE");
        return l;
    }

    public static Location createSavedLocation(Tenant tenant) {
        Location l = createLocation(tenant);
        l.setId(1L);
        return l;
    }

    public static Meter createMeter(Tenant tenant, Location location) {
        Meter m = new Meter();
        m.setTenant(tenant);
        m.setLocation(location);
        m.setType(EnergyType.ELECTRICITY);
        return m;
    }

    public static Meter createSavedMeter(Tenant tenant, Location location) {
        Meter m = createMeter(tenant, location);
        m.setId(1L);
        return m;
    }

    public static MeterReading createMeterReading(Tenant tenant, Meter meter) {
        MeterReading mr = new MeterReading();
        mr.setTenant(tenant);
        mr.setMeter(meter);
        mr.setValue(new BigDecimal("100.00"));
        mr.setUnit(EnergyUnit.KWH);
        return mr;
    }

    public static MeterReading createSavedMeterReading(Tenant tenant, Meter meter) {
        MeterReading mr = createMeterReading(tenant, meter);
        mr.setId(1L);
        return mr;
    }
}
