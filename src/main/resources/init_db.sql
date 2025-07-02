create type public.contact_type as enum ('MOBILE_PHONE', 'MAIL', 'TELEGRAM', 'LINKEDIN', 'GITHUB', 'STACKOVERFLOW');

create type public.section_type as enum ('PERSONAL', 'OBJECTIVE', 'ACHIEVEMENT', 'QUALIFICATIONS', 'EXPERIENCE', 'EDUCATION');

create table public.resume
(
    uuid      uuid not null
        constraint resume_pk
            primary key,
    full_name text not null
);

create table public.contact
(
    id            serial
        constraint contact_pk
            primary key,
    resume_uuid   uuid         not null
        references public.resume
            on update restrict on delete cascade,
    type_contact  contact_type not null,
    value_contact text         not null
);

create unique index contact_uuid_type_index
    on public.contact (resume_uuid, type_contact);

create table public.section
(
    id            serial
        constraint section_pk
            primary key,
    resume_uuid   uuid
        constraint section_resume_uuid_fk
            references public.resume
            on update restrict on delete cascade,
    type_section  section_type not null,
    value_section jsonb        not null
);

create index section_resume_uuid_type_section_index
    on public.section (resume_uuid, type_section);


