package controllers

import java.util.UUID

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import util.OAuth2

object Application extends Controller {

  def index = Action { implicit request =>
    val oauth2 = new OAuth2(Play.current)
    val callbackUrl = util.routes.OAuth2.callback(None, None).absoluteURL()
    val scope = "repo" // github scope - request repo access
  val state = UUID.randomUUID().toString // random confirmation string
  val redirectUrl = oauth2.getAuthorizationUrl(callbackUrl, scope, state)
    Ok(views.html.index("Your new application is ready.", redirectUrl)).
      withSession("oauth-state" -> state)
  }

  def mobile = Action.async {
    Future {
      Ok(views.html.mobile())
    }
  }

  def jsonHymns = Action {
    //    val hymns = None;
    Ok(views.html.hymns.list(???))
  }

}