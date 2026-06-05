--liquibase formatted sql

--changeset burgasvv:1
create table if not exists project
(
    id          uuid default gen_random_uuid() unique not null,
    name        varchar unique                        not null,
    description text                                  not null,
    course_id   uuid references course (id) on delete cascade on update cascade,
    task_id     uuid unique references file (id) on delete cascade on update cascade
);