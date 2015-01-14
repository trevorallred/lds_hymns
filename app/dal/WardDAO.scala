package dal

import models.Ward
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class WardDAO(_dal: SlickDAL) extends BaseDAO(_dal) {

  import dal.Wards
  import dal.profile.simple._

  def save(row: Ward): Future[Ward] = {
    withTransaction { implicit session =>
      val result = for {
        p <- Wards if p.unitNo === row.unitNo
      } yield p

      Future {
        result.list.headOption.map(x =>
          update(row)
        ).getOrElse(
            insert(row)
          )
      }
    }
  }

  private def insert(row: Ward)(implicit session: SlickSession) = {
    Wards += row
    row
  }

  private def update(row: Ward)(implicit session: SlickSession) = {
    val q = for {existing <- Wards if existing.unitNo === row.unitNo}
    yield (existing.name, existing.stakeUnitNo)
    q.update((row.name, row.stakeUnitNo.get))
    row
  }

}
