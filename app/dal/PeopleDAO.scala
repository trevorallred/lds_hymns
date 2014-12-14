package dal

import models.Person

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class PeopleDAO(_dal: SlickDAL) extends BaseDAO(_dal) {

  import dal.profile.simple._
  import dal.People

  def all(): Future[Set[Person]] = {
    withSession { implicit session =>
      val q = for { p <- People } yield p
      Future(q.list.toSet)
    }
  }

  def findByLastNamePrefix(prefix: String): Future[Set[Person]] = {
    withSession { implicit session =>
      val lcPrefix = prefix.toLowerCase
      val q = for { p <- People if p.lastName.toLowerCase.startsWith(prefix) } yield p
      Future(q.list.toSet)
    }
  }

  def findByID(id: Int): Future[Option[Person]] = {
    withSession { implicit session =>
      val q = for { p <- People if p.id === id } yield p
      Future(q.list.headOption)
    }
  }

  def total(): Future[Int] = {
    withSession { implicit session =>
      Future {
        People.list.length
      }
     }
  }

  def save(p: Person): Future[Person] = {
    withTransaction { implicit session =>
      Future {
        p.id.map { id => update(p) }.getOrElse(insert(p))
      }
    }
  }

  private def insert(person: Person)(implicit session: SlickSession) = {
    val id = (People returning People.map(_.id)) += person
    person.copy(id = Some(id))
  }

  private def update(person: Person)(implicit session: SlickSession) = {
    val q = for { p <- People if p.id === person.id.get }
            yield (p.firstName, p.middleName, p.lastName, p.ssn, p.gender, p.birthDate)
    q.update((person.firstName, person.middleName, person.lastName,
              person.ssn, person.gender, person.birthDate))
    person
  }
}
