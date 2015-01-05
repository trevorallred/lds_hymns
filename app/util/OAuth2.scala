package util

import play.api.Application
import play.api.Play
import play.api.http.{MimeTypes, HeaderNames}
import play.api.libs.ws.WS
import play.api.mvc.{Results, Action, Controller}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OAuth2(application: Application) {
  lazy val ldsAuthId = application.configuration.getString("lds.client.id").get
  lazy val ldsAuthSecret = application.configuration.getString("lds.client.secret").get

  def getAuthorizationUrl(redirectUri: String, scope: String, state: String): String = {
    val authEndpoint = application.configuration.getString("lds.redirect.url").get
    val responseType = "code"
    authEndpoint.format(responseType, ldsAuthId, redirectUri, scope, state)
  }

  def getToken(code: String): Future[String] = {
    val tokenEndpoint = application.configuration.getString("lds.token.endpoint").get
    val tokenResponse = WS.url(tokenEndpoint)(application).
      withQueryString("client_id" -> ldsAuthId,
        "client_secret" -> ldsAuthSecret,
        "code" -> code).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(Results.EmptyContent())

    tokenResponse.flatMap { response =>
      println("Status: " + response.status)
      println(response.body)
      (response.json \ "access_token").asOpt[String].fold(
        Future.failed[String](new IllegalStateException(response.json.toString))
      ) { accessToken =>
        Future.successful(accessToken)
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
      oauthState <- request.session.get("oauth-state")
    } yield {
      if (state == oauthState) {
        oauth2.getToken(code).map { accessToken =>
          Redirect(util.routes.OAuth2.success()).withSession("oauth-token" -> accessToken)
        }.recover {
          case ex: IllegalStateException => Unauthorized(ex.getMessage)
        }
      }
      else {
        Future.successful(BadRequest("Invalid lds login"))
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  def success() = Action.async { request =>
    implicit val app = Play.current
    request.session.get("oauth-token").fold(Future.successful(Unauthorized("No way Jose"))) { authToken =>
      WS.url("https://ldsconnect.org/api/ldsorg/me").
        withHeaders(HeaderNames.AUTHORIZATION -> s"token $authToken").
        get().map { response =>
          Ok(response.json)
        }
    }
  }
}