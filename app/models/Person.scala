package models

import java.util.Date

case class Person(id:         Option[Int],
                  firstName:  String,
                  middleName: Option[String],
                  lastName:   String,
                  ssn:        String,
                  gender:     String,
                  birthDate:  Date) {
  val fullName =
    Seq(Some(firstName), middleName, Some(lastName)).flatten.mkString(" ")
}

