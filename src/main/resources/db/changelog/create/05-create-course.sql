--liquibase formatted sql

--changeset burgasvv:1
create table if not exists course (
    id uuid default gen_random_uuid() unique not null ,
    name varchar unique not null ,
    description text not null
);