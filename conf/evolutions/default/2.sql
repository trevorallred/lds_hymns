# --- !Ups

CREATE TABLE ward (
  unitNo int(11) NOT NULL,
  name varchar(100) NOT NULL,
  stakeUnitNo int(11) NOT NULL,
  PRIMARY KEY (unitNo),
  UNIQUE KEY SECONDARY (stakeUnitNo, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS ward;
