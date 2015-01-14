package dal

import scala.slick.driver.JdbcProfile

trait SlickProfile {
  val profile: JdbcProfile
}

class SlickDAL(override val profile: JdbcProfile)
  extends SlickProfile
  with StakeComponent
  with WardComponent
  with MemberComponent

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

    def areaUnitNo = column[Int]("areaUnitNo")

    def * = (unitNo, name, areaUnitNo.?) <>(Stake.tupled, Stake.unapply _)
  }

  val Stakes = TableQuery[StakeTable]
}

trait WardComponent {
  self: SlickProfile =>

  import profile.simple._
  import models.Ward

  class WardTable(tag: Tag) extends Table[Ward](tag, "ward") {
    def unitNo = column[Int]("unitNo", O.PrimaryKey)

    def name = column[String]("name")

    def stakeUnitNo = column[Int]("stakeUnitNo")

    def * = (unitNo, name, stakeUnitNo.?) <>(Ward.tupled, Ward.unapply _)
  }

  val Wards = TableQuery[WardTable]
}

trait MemberComponent {
  self: SlickProfile =>

  import profile.simple._
  import models.Member

  class MemberTable(tag: Tag) extends Table[Member](tag, "member") {
    def memberID = column[Long]("memberID", O.PrimaryKey)

    def name = column[String]("name")

    def surname = column[String]("surname")

    def wardUnitNo = column[Int]("wardUnitNo")

    def * = (memberID, name, surname, wardUnitNo) <>(Member.tupled, Member.unapply _)
  }

  val Members = TableQuery[MemberTable]
}
