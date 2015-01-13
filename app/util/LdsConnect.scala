package util

import play.api.Play
import play.api.http.HeaderNames
import play.api.libs.json.{JsNumber, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.Action
import services.LdsSession
import util.OAuth2._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class LdsProfile(memberID: Integer, name: String, surname: String, gender: String, email: String, phone: String) {
  override def toString: String = memberID + name
}

object LdsConnect {
  def profile = Action.async { request =>
    implicit val app = Play.current
    LdsSession.getToken(request).fold(Future.successful(Unauthorized(oauth2.NOT_LOGGED_IN))) { authToken =>
      WS.url("https://ldsconnect.org/api/ldsorg/me").
        withHeaders(HeaderNames.AUTHORIZATION -> s"Bearer $authToken").
        get().map { response =>
        val currentUser = convertProfileFromJson(response.json)
        println(currentUser)
        Ok(response.json)
      }
    }
  }

  def convertProfileFromJson(json: JsValue): LdsProfile = {
    new LdsProfile(
      memberID = (json \ "currentUserId").as[Int],
      name = (json \ "currentUserId").as[String],
      surname = "",
      gender = "",
      email = "",
      phone = ""
    )
  }

  def ward = Action.async { request =>
    implicit val app = Play.current
    val stakeUnitNo = 518743
    val wardUnitNo = 189219
    LdsSession.getToken(request).fold(Future.successful(Unauthorized(oauth2.NOT_LOGGED_IN))) { authToken =>
      val wardUrl = s"https://ldsconnect.org/api/ldsconnect/stakes/$stakeUnitNo/wards/$wardUnitNo/info"
      //      val wardUrl = s"https://ldsconnect.org/api/ldsconnect/stakes/$stakeUnitNo/info"
      println(wardUrl)
      WS.url(wardUrl).
        withHeaders(HeaderNames.AUTHORIZATION -> s"Bearer $authToken").
        get().map { response =>
        Ok(response.json \ "members")
      }
    }
  }

}
