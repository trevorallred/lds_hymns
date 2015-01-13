package util

import play.api.libs.ws.{WS, WSAuthScheme}
import play.api.mvc.{Action, Controller}
import play.api.{Application, Play}
import services.LdsSession

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OAuth2(application: Application) {
  val NOT_LOGGED_IN = "Not Logged In"
  lazy val ldsAuthId = application.configuration.getString("lds.client.id").get
  lazy val ldsAuthSecret = application.configuration.getString("lds.client.secret").get

  def getAuthorizationUrl(redirectUri: String, responseType: String, scope: String, state: String): String = {
    val authEndpoint = application.configuration.getString("lds.redirect.url").get
    authEndpoint.format(responseType, ldsAuthId, redirectUri, scope, state)
  }

  def getNewToken(code: String): Future[String] = {
    val tokenEndpoint = application.configuration.getString("lds.token.endpoint").get
    val tokenResponse = WS.url(tokenEndpoint)(application).
      withAuth(ldsAuthId, ldsAuthSecret, WSAuthScheme.BASIC).
      post(Map(
      "grant_type" -> Seq("authorization_code"),
      "redirect_uri" -> Seq("http://lds.trevorallred.com:9000/_oauth-callback"),
      "code" -> Seq(code)
    ))

    tokenResponse.flatMap { response =>
      if (response.status == 200) {
        (response.json \ "access_token").asOpt[String].fold(
          Future.failed[String](new IllegalStateException(response.json.toString))
        ) { accessToken =>
          Future.successful(accessToken)
        }
      } else {
        Future.failed[String](new IllegalStateException(response.status + ": " + response.body))
      }
    }
  }
}

object OAuth2 extends Controller {
  lazy val oauth2 = new OAuth2(Play.current)

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async { implicit request =>
    (for {
      code <- codeOpt
      state <- stateOpt
      oauthState <- request.session.get(LdsSession.OAUTH_STATE)
    } yield {
      if (state == oauthState) {
        oauth2.getNewToken(code).map { accessToken =>
          Redirect(controllers.routes.Application.index).withSession(LdsSession.OAUTH_TOKEN -> accessToken)
        }.recover {
          case ex: IllegalStateException => Unauthorized(ex.getMessage)
        }
      } else {
        Future.successful(BadRequest("Your session has changed. Try again."))
      }
    }).getOrElse(
        Future.successful(BadRequest("Missing either code or state or session state parameters"))
      )
  }

}