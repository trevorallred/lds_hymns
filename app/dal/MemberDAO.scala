package dal

import models.{Member}

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class MemberDAO(_dal: SlickDAL) extends BaseDAO(_dal) {
  import dal.Members
  import dal.profile.simple._

  def save(row: Member): Future[Member] = {
    withTransaction { implicit session =>
      val result = for {
        p <- Members if p.memberID === row.memberID
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

  private def insert(row: Member)(implicit session: SlickSession) = {
    Members += row
    row
  }

  private def update(row: Member)(implicit session: SlickSession) = {
    val q = for {existing <- Members if existing.memberID === row.memberID}
    yield (existing.name, existing.surname, existing.wardUnitNo)
    q.update((row.name, row.surname, row.wardUnitId))
    row
  }
}
