create sequence location_seq start with 1 increment by 50;
create sequence meter_reading_seq start with 1 increment by 50;
create sequence meter_seq start with 1 increment by 50;

create table tenant (
    created_at timestamp(6) with time zone not null,
    id uuid not null,
    name varchar(100) not null,
    primary key (id)
);

create table app_user (
    is_active boolean not null,
    created_at timestamp(6) with time zone not null,
    id uuid not null,
    tenant_id uuid not null,
    display_name varchar(255),
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    role varchar(255) not null,
    primary key (id)
);

create table location (
    country_code varchar(3) not null,
    postal_code integer not null,
    street_number integer not null,
    id bigint not null,
    tenant_id uuid not null,
    city varchar(100) not null,
    street varchar(100) not null,
    primary key (id)
);

create table meter (
    id bigint not null,
    location_id bigint not null,
    tenant_id uuid not null,
    type varchar(255) check ((type in ('ELECTRICITY','GAS'))),
    name varchar(100),
    primary key (id)
);

create table meter_reading (
    unit smallint not null check ((unit between 0 and 1)),
    value numeric(38,2) not null,
    taken_at timestamp(6) with time zone not null,
    id bigint not null,
    meter_id bigint not null,
    tenant_id uuid not null,
    primary key (id)
);

alter table if exists app_user add constraint fk_app_user_tenant_id foreign key (tenant_id) references tenant;
alter table if exists location add constraint fk_location_tenant_id foreign key (tenant_id) references tenant;
alter table if exists meter add constraint fk_meter_location_id foreign key (location_id) references location;
alter table if exists meter add constraint fk_meter_tenant_id foreign key (tenant_id) references tenant;
alter table if exists meter_reading add constraint fk_meter_reading_meter_id foreign key (meter_id) references meter;
alter table if exists meter_reading add constraint fk_meter_reading_tenant_id foreign key (tenant_id) references tenant;
