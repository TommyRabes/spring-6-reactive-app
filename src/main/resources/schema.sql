CREATE TABLE IF NOT EXISTS Beer
(
    id                  integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
    beer_name           varchar(255),
    beer_style          varchar(255),
    upc                 varchar(25),
    quantity_on_hand    integer,
    price               decimal,
    created_date        timestamp,
    last_modified_date  timestamp
);

CREATE TABLE IF NOT EXISTS Customer
(
    id                  integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name          varchar(255) NOT NULL,
    last_name           varchar(255) NOT NULL,
    email               varchar(320) NOT NULL,
    birthdate           date NOT NULL,
    created_date        timestamp,
    last_modified_date  timestamp
);