CREATE TABLE pets
(
    id                     integer      NOT NULL PRIMARY KEY,
    name                   VARCHAR(255) NOT NULL UNIQUE,
    tagname                    VARCHAR(255) NOT NULL
);