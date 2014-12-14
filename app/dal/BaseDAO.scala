package dal

import app.Global

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.control.NonFatal

class BaseDAO(val dal: SlickDAL) {

  import scala.slick.jdbc.JdbcBackend

  type SlickSession = JdbcBackend#Session

  def withSession[T](code: SlickSession => Future[T]): Future[T] = {
    val session = Global.database.createSession()
    code(session) map { result =>
      session.close()
      result
    } recoverWith {
      case NonFatal(e) => {
        session.close()
        Future.failed(e)
      }
    }
  }

  def withTransaction[T](code: SlickSession => Future[T]): Future[T] = {
    withSession { session =>
      session.withTransaction {
        code(session)
      }
    }
  }
}
