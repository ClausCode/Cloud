-- UsersTable
create table if not exists "users"
(
    "id"       bigserial primary key,
    "email"    varchar(128) unique not null,
    "name"     varchar(128)        not null,
    "password" varchar(1024)       not null,
    "secret"   varchar(64)         not null,
    "tfa"      bool                not null,
    "created"  timestamp           not null,
    "updated"  timestamp           not null,
    "status"   varchar(16)
);

-- AuthTable
create table if not exists "auth"
(
    "id"      bigserial primary key,
    "user_id" bigserial references "users" ("id") on delete cascade,
    "browser" varchar(36) not null unique,
    "created" timestamp   not null,
    "updated" timestamp   not null
);

--RolesTable
create table if not exists "roles"
(
    "id"   bigserial primary key,
    "name" varchar(64) not null unique
);

insert into "roles" ("name") values ('ADMIN');
insert into "roles" ("name") values ('DEFAULT');

--UserRolesTable
create table if not exists "user_roles"
(
    "user_id" bigserial references "users" ("id") on delete cascade,
    "role_id" bigserial references "roles" ("id") on delete cascade
);