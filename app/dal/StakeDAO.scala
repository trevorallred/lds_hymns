package dal

import models.Stake

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class StakeDAO(_dal: SlickDAL) extends BaseDAO(_dal) {

  import dal.profile.simple._
  import dal.Stakes

  def all(): Future[Set[Stake]] = {
    withSession { implicit session =>
      val q = for {p <- Stakes} yield p
      Future(q.list.toSet)
    }
  }

  def findByLastNamePrefix(prefix: String): Future[Set[Stake]] = {
    withSession { implicit session =>
      val lcPrefix = prefix.toLowerCase
      val q = for {p <- Stakes if p.name.toLowerCase.startsWith(prefix)} yield p
      Future(q.list.toSet)
    }
  }

  def findByID(unitNo: Int): Future[Option[Stake]] = {
    withSession { implicit session =>
      val q = for {p <- Stakes if p.unitNo === unitNo} yield p
      Future(q.list.headOption)
    }
  }

  def total(): Future[Int] = {
    withSession { implicit session =>
      Future {
        Stakes.list.length
      }
    }
  }

  def save(p: Stake): Future[Stake] = {
    withTransaction { implicit session =>
      findByID(p.unitNo).map { opt =>
        opt.map { existing => update(p)}
          .getOrElse(insert(p))
      }
    }
  }

  private def insert(row: Stake)(implicit session: SlickSession) = {
    Stakes += row
    row
  }

  private def update(row: Stake)(implicit session: SlickSession) = {
    val q = for {existing <- Stakes if existing.unitNo === row.unitNo}
    yield (existing.name)
    q.update((row.name))
    row
  }
}
