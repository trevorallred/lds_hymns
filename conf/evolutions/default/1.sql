# --- !Ups

CREATE TABLE stake (
  unitNo int(11) NOT NULL,
  name varchar(100) NOT NULL,
  PRIMARY KEY (unitNo),
  KEY SECONDARY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS stake;
