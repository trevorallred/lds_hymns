package dal

import scala.slick.driver.JdbcProfile

trait SlickProfile {
  val profile: JdbcProfile
}

class SlickDAL(override val profile: JdbcProfile)
  extends SlickProfile
  with StakeComponent

trait StakeComponent {
  self: SlickProfile =>

  import profile.simple._
  import models.Stake

  implicit val JavaUtilDateMapper =
    MappedColumnType.base[java.util.Date, java.sql.Timestamp](
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime)
    )

  class StakeTable(tag: Tag) extends Table[Stake](tag, "stake") {
    def unitNo = column[Int]("unitNo", O.PrimaryKey)

    def name = column[String]("name")

    def * = (unitNo, name) <>
      (Stake.tupled, Stake.unapply _)
  }

  val Stakes = TableQuery[StakeTable]
}
