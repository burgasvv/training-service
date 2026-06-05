--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity_course
(
    identity_id uuid references identity (id) on delete cascade on update cascade,
    course_id   uuid references course (id) on delete cascade on update cascade,
    primary key (identity_id, course_id)
);