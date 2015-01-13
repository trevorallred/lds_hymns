package services

import play.api.mvc.RequestHeader

class CurrentUser (memberId: String) {
}

object LdsSession {
  def getCurrentUser(request: RequestHeader) = {
    getToken(request)
  }

  val OAUTH_TOKEN = "oauth-token"
  val OAUTH_STATE = "oauth-state"
  val CURRENT_USER = "current-user"

  def isLoggedIn(request: RequestHeader): Boolean = {
    getToken(request).isDefined
  }

  def getToken(request: RequestHeader) = {
    request.session.get(LdsSession.OAUTH_TOKEN)
  }



}
