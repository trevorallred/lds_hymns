package util

import dal.DAO
import models.{Ward, Stake}
import play.api.Play
import play.api.http.HeaderNames
import play.api.libs.json.{JsNumber, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.Action
import services.LdsSession
import util.OAuth2._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class LdsProfile(
                       memberID: Integer,
                       name: String,
                       surname: String,
                       gender: String,
                       email: String,
                       phone: Option[String],
                       ward: Option[Ward],
                       stake: Option[Stake]) {
  override def toString: String = s":$name $surname ($memberID) in $ward in $stake"
}

object LdsConnect {
  def profile = Action.async { request =>
    implicit val app = Play.current
    LdsSession.getToken(request).fold(Future.successful(Unauthorized(oauth2.NOT_LOGGED_IN))) { authToken =>
      WS.url("https://ldsconnect.org/api/ldsorg/me").
        withHeaders(HeaderNames.AUTHORIZATION -> s"Bearer $authToken").
        get.map { response =>
        val currentUser = convertProfileFromJson(response.json)
        //        DAO.stakeDAO.save(new Stake(unitNo = scala.util.Random.nextInt(100000), name = "123"))
        println(currentUser)
        if (currentUser.stake.isDefined) {
          DAO.stakeDAO.save(currentUser.stake.get)
        }
        Ok(response.json)
      }
    }
  }

  def convertProfileFromJson(json: JsValue): LdsProfile = {
    new LdsProfile(
      memberID = (json \ "currentUserId").as[Int],
      name = "",
      //      name = (json \ "currentUserId").as[String],
      surname = "",
      gender = "",
      email = "",
      phone = None,
      ward = None,
      stake = convertStakeFromCurrentUnitsJson(json \ "currentUnits")
    )
  }

  private def convertStakeFromCurrentUnitsJson(currentUnits: JsValue): Option[Stake] = {
    val hasStake = (currentUnits \ "stake").as[Boolean]
    if (hasStake) {
      Some(new Stake((currentUnits \ "stakeUnitNo").as[Int],
        (currentUnits \ "stakeName").as[String],
        Some((currentUnits \ "areaUnitNo").as[Int])))
    } else {
      None
    }
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
