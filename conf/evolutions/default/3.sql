# --- !Ups

CREATE TABLE member (
  memberID BIGINT NOT NULL,
  name varchar(100) NOT NULL,
  surname varchar(100) NOT NULL,
  wardUnitNo int(11) NOT NULL,
  adult tinyint NOT NULL,
  male tinyint NOT NULL,
  active tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (memberID),
  UNIQUE KEY SECONDARY (wardUnitNo, surname, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS member;
