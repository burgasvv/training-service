--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity
(
    id         uuid                    default gen_random_uuid() unique not null,
    authority  varchar        not null default 'USER',
    email      varchar unique not null,
    password   varchar        not null,
    status     boolean        not null default true,
    firstname  varchar        not null,
    lastname   varchar        not null,
    patronymic varchar        not null,
    about      text           not null,
    image_id   uuid unique references image (id) on delete cascade on update cascade
);

create index idx_identity_authority on identity (authority);
create index idx_identity_status on identity (status);
create index idx_identity_fio on identity (firstname, lastname, patronymic);