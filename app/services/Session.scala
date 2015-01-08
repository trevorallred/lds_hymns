package services

import play.api.mvc.RequestHeader

class LdsSession(request: RequestHeader) {
  def isLogged: Boolean = {
    !getToken().isEmpty
  }

  def getToken() = {
    request.session.get(Session.OAUTH_TOKEN)
  }
}

object Session {
  val OAUTH_TOKEN = "oauth-token"
  val OAUTH_STATE = "oauth-state"

  def getToken(request: RequestHeader) = {
    request.session.get(Session.OAUTH_TOKEN)
  }

}
