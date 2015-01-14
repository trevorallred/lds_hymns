package models

case class Stake(unitNo: Int,
                 name: String,
                 areaUnitNo: Option[Int])

case class Ward(unitNo: Int,
                name: String,
                stakeUnitNo: Option[Int])
