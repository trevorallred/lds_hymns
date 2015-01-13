package controllers

import java.util.UUID

import play.api._
import play.api.mvc._
import services.LdsSession
import util.OAuth2

object Application extends Controller {

  def index = Action { implicit request =>
    if (LdsSession.isLoggedIn(request)) {
      Ok(views.html.index())
    } else {
      val callbackUrl = util.routes.OAuth2.callback(None, None).absoluteURL()
      val state = UUID.randomUUID().toString
      val oauth2 = new OAuth2(Play.current)
      val redirectUrl = oauth2.getAuthorizationUrl(callbackUrl, "code", "", state)
      Ok(views.html.login(redirectUrl)).
        withSession("oauth-state" -> state)
    }
  }

  def logout = Action { implicit request =>
    Redirect(controllers.routes.Application.index).withNewSession
  }

  def stub = Action {
    Ok("not implemented yet")
  }

  def stub2(variable: String) = Action {
    Ok("not implemented yet")
  }
}