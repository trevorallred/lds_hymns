package util

import dal.DAO
import models.{Stake, Ward}
import play.api.Play
import play.api.http.HeaderNames
import play.api.libs.json.JsValue
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
        val currentUser = LdsConnectJson.convertProfileFromJson(response.json)
        println(currentUser)
        currentUser.stake.map(stake =>
          DAO.stakeDAO.save(stake)
        )
        currentUser.ward.map(ward =>
          //          DAO.wardDAO.save(ward)
          println(ward)
        )
        Ok(response.json)
      }
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

object LdsConnectJson {
  private val CURRENT_UNITS = "currentUnits"
  private val AREA_UNIT_NO = "areaUnitNo"
  private val STAKE = "stake"
  private val STAKE_NAME = "stakeName"
  private val STAKE_UNIT_NO = "stakeUnitNo"
  private val WARD = "ward"
  private val WARD_NAME = "wardName"
  private val WARD_UNIT_NO = "wardUnitNo"

  def convertProfileFromJson(json: JsValue): LdsProfile = {
    new LdsProfile(
      memberID = (json \ "currentUserId").as[Int],
      name = "",
      //      name = (json \ "currentUserId").as[String],
      surname = "",
      gender = "",
      email = "",
      phone = None,
      ward = convertWardFromCurrentUnitsJson(json \ CURRENT_UNITS),
      stake = convertStakeFromCurrentUnitsJson(json \ CURRENT_UNITS)
    )
  }

  private def convertStakeFromCurrentUnitsJson(currentUnits: JsValue): Option[Stake] = {
    if ((currentUnits \ STAKE).as[Boolean]) {
      Some(new Stake(
        (currentUnits \ STAKE_UNIT_NO).as[Int],
        (currentUnits \ STAKE_NAME).as[String],
        Some((currentUnits \ AREA_UNIT_NO).as[Int])
      ))
    } else {
      None
    }
  }

  private def convertWardFromCurrentUnitsJson(currentUnits: JsValue): Option[Ward] = {
    if ((currentUnits \ WARD).as[Boolean]) {
      Some(new Ward(
        (currentUnits \ WARD_UNIT_NO).as[Int],
        (currentUnits \ WARD_NAME).as[String],
        Some((currentUnits \ STAKE_UNIT_NO).as[Int])
      ))
    } else {
      None
    }
  }
}