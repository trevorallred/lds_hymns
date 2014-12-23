package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.control.NonFatal

object Application extends Controller {

  def index = Action.async {
    Future {
      Ok(views.html.mobile())
    }
  }

  def jsonHymns = Action {
//    val hymns = None;
    Ok(views.html.hymns.list(???))
  }

}