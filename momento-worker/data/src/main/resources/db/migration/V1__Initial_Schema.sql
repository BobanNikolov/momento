create table user_account
(
    id            bigserial primary key,
    role          varchar(20)  not null,
    email         varchar(255) not null unique,
    password_hash varchar(255) not null,
    status        varchar(20)  not null,
    created_at    timestamp    not null default current_timestamp
);

create table rekognition_collection
(
    id                bigserial primary key,
    aws_collection_id varchar(255) not null,
    status            varchar(20)  not null,
    created_at        timestamp    not null default current_timestamp,
    deleted_at        timestamp
);

create table event
(
    id             bigserial primary key,
    name           varchar(255) not null,
    slug           varchar(255) not null unique,
    event_date     date,
    location       varchar(255),
    status         varchar(20)  not null,
    collection_id  bigint references rekognition_collection (id),
    retention_days integer,
    created_at     timestamp    not null default current_timestamp,
    expires_at     timestamp
);

-- adding event_id to rekognition_collections as requested by schema
alter table rekognition_collection
    add column event_id bigint references event (id);

create table event_photographer
(
    id              bigserial primary key,
    event_id        bigint    not null references event (id),
    photographer_id bigint    not null references user_account (id),
    assigned_at     timestamp not null default current_timestamp
);

create table photo
(
    id                bigserial primary key,
    event_id          bigint       not null references event (id),
    uploaded_by       bigint       not null references user_account (id),
    original_s3_key   varchar(255) not null,
    thumbnail_s3_key  varchar(255),
    file_name         varchar(255),
    content_type      varchar(100),
    file_size         bigint,
    processing_status varchar(20)  not null,
    created_at        timestamp    not null default current_timestamp
);

create table rekognition_face
(
    id                bigserial primary key,
    event_id          bigint       not null references event (id),
    photo_id          bigint       not null references photo (id),
    face_id           varchar(255) not null,
    external_image_id varchar(255),
    bounding_box      text,
    confidence        float,
    created_at        timestamp    not null default current_timestamp
);

create table guest_search
(
    id                     bigserial primary key,
    event_id               bigint    not null references event (id),
    selfie_s3_key          varchar(255),
    consent_accepted       boolean   not null,
    consent_policy_version varchar(50),
    searched_at            timestamp,
    result_count           integer,
    created_at             timestamp not null default current_timestamp,
    selfie_deleted_at      timestamp
);

create table download
(
    id              bigserial primary key,
    event_id        bigint    not null references event (id),
    photo_id        bigint    not null references photo (id),
    guest_search_id bigint references guest_search (id),
    downloaded_at   timestamp not null default current_timestamp,
    ip_address      varchar(45),
    user_agent      text
);

create table organizer_agreement
(
    id                bigserial primary key,
    organizer_id      bigint      not null references user_account (id),
    event_id          bigint      not null references event (id),
    agreement_version varchar(50) not null,
    accepted_at       timestamp   not null default current_timestamp,
    accepted_ip       varchar(45)
);
