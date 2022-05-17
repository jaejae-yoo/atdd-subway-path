DROP TABLE SECTION IF EXISTS;
DROP TABLE STATION IF EXISTS;
DROP TABLE LINE IF EXISTS;

create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    up_station_id bigint not null,
    down_station_id bigint not null,
    distance int,
    primary key(id),
    foreign key (line_id) references LINE(id) ON DELETE CASCADE ON UPDATE CASCADE,
    foreign key (up_station_id) references STATION(id),
    foreign key (down_station_id) references STATION(id)
);
