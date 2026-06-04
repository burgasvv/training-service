--liquibase formatted sql

--changeset burgasvv:1
create table if not exists file
(
    id           uuid             default gen_random_uuid() unique not null,
    name         varchar not null,
    content_type varchar not null,
    size         bigint  not null default 0 check ( size >= 0 ),
    data         bytea   not null
);

create index idx_file_name on file(name);