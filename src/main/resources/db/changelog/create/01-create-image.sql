--liquibase formatted sql

--changeset burgasvv:1
create table if not exists image
(
    id           uuid    default gen_random_uuid() unique not null,
    name         varchar                                  not null,
    content_type varchar                                  not null,
    size         bigint                                   not null default 0 check ( size >= 0 ),
    preview      boolean default false                    not null,
    data         bytea                                    not null
);

create index idx_image_name on image(name);
create index idx_image_preview on image(preview);