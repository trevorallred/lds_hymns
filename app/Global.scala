package app

import dal.SlickDAL
import play.api.{Application, GlobalSettings}

import scala.slick.driver.H2Driver

object Global extends GlobalSettings {
  // No choice but to use vars here.
  private var _dal: Option[SlickDAL] = None
  private var _db: Option[scala.slick.jdbc.JdbcBackend#Database] = None

  def dal = _dal.get // Okay to barf if not there
  def database = _db.get

  override def onStart(app: Application): Unit = {
    super.onStart(app)

    initDB(app)
  }

  private def initDB(app: Application): Unit = {
    import scala.slick.jdbc.JdbcBackend.Database

    val cfg      = app.configuration
    val driver   = cfg.getString("db.default.driver")
    val url      = cfg.getString("db.default.url")
    val user     = cfg.getString("db.default.user")
    val password = cfg.getString("db.default.password")

    if (Seq(driver, url, user, password).flatten.length == 0) {
      sys.error("Missing required database parameters in configuration.")
    }

    val (dal: SlickDAL, db: Database) = driver.get match {
      case "org.h2.Driver" => {
        (new SlickDAL(H2Driver),
         Database.forURL(url.get,
                         driver   = driver.get,
                         user     = user.get,
                         password = password.get))
      }

      case _ => {
        sys.error(s"Unsupported database type: $driver.get")
      }
    }

    _dal = Some(dal)
    _db  = Some(db)
  }
}
