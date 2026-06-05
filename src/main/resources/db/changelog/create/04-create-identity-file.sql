--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity_file
(
    identity_id uuid references identity (id) on delete cascade on update cascade,
    file_id     uuid references file (id) on delete cascade on update cascade,
    primary key (identity_id, file_id)
);