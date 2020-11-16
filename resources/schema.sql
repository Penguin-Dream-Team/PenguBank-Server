create table users(
    id serial primary key,
    email varchar(200) not null,
    password varchar(60) not null,
    registered_at timestamptz not null default current_timestamp
);

create table accounts(
    id serial primary key,
    balance int not null default 1000,
    user_id int not null,

    constraint fk_user foreign key(user_id) references users(id) on delete cascade
);
