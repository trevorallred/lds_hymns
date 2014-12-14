package dal

import java.sql.Timestamp
import java.util.Date

import scala.slick.driver.JdbcProfile

trait SlickProfile {
  val profile: JdbcProfile
}

class SlickDAL(override val profile: JdbcProfile)
  extends SlickProfile
  with PeopleComponent

trait PeopleComponent {
  self: SlickProfile =>

  import profile.simple._
  import models.Person

  implicit val JavaUtilDateMapper =
    MappedColumnType.base[java.util.Date, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime)
    )

  class PeopleTable(tag: Tag) extends Table[Person](tag, "PEOPLE") {
    def id         = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName  = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName   = column[String]("LAST_NAME")
    def ssn        = column[String]("SSN")
    def gender     = column[String]("GENDER")
    def birthDate  = column[Date]("BIRTH_DATE")

    def * = (id.?, firstName, middleName, lastName, ssn, gender, birthDate) <>
            (Person.tupled, Person.unapply _)
  }

  val People = TableQuery[PeopleTable]
}
