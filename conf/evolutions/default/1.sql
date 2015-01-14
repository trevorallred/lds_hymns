# --- !Ups

CREATE TABLE stake (
  unitNo int(11) NOT NULL,
  name varchar(100) NOT NULL,
  areaUnitNo int(11) NULL,
  PRIMARY KEY (unitNo),
  UNIQUE KEY SECONDARY (name, areaUnitNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS stake;
