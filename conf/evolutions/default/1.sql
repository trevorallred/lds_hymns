# --- !Ups

CREATE TABLE IF NOT EXISTS people (
  id          INT NOT NULL IDENTITY PRIMARY KEY,
  first_name  VARCHAR(128) NOT NULL,
  middle_name VARCHAR(128) NULL,
  last_name   VARCHAR(128) NOT NULL,
  ssn         VARCHAR(11) NOT NULL,
  gender      CHAR(1) NOT NULL,
  birth_date  DATE NOT NULL,
)

# --- !Downs

DROP TABLE IF EXISTS people;
