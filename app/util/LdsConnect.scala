package util

import dal.DAO
import models.{Member, Stake, Ward}
import play.api.Play
import play.api.http.HeaderNames
import play.api.libs.json.{Json, JsObject, JsArray, JsValue}
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
        val currentUser = LdsProfileJson.convertProfileFromJson(response.json)
        println(currentUser)
        currentUser.stake.map(stake =>
          DAO.stakeDAO.save(stake)
        )
        currentUser.ward.map(ward =>
          DAO.wardDAO.save(ward)
        )
        Ok(response.json)
      }
    }
  }

  def getMemberData(authToken: String, stakeUnitNo: Int, wardUnitNo: Int): Future[List[JsObject]] = {
    implicit val app = Play.current
    val wardUrl = s"https://ldsconnect.org/api/ldsconnect/stakes/$stakeUnitNo/wards/$wardUnitNo/info"
    WS.url(wardUrl)
      .withHeaders(HeaderNames.AUTHORIZATION -> s"Bearer $authToken")
      .get()
      .map { response =>
      val values = (response.json \ "members").as[JsObject].values
      println(values)
      println(values.size)
      var counter = 0
      values.foreach { v =>
        counter = counter + 1
        val m = Member(
          (v.as[JsObject] \ "id").asOpt[Long].getOrElse {
            (v.as[JsObject] \ "id").toString.replaceAll( """[^\d]""", "").toLong
          },
          (v.as[JsObject] \ "name").asOpt[String].getOrElse(""),
          (v.as[JsObject] \ "surname").asOpt[String].getOrElse(""),
          wardUnitNo
        )
//        println(m)
        DAO.memberDAO.save(m)
        println(counter + " SAVED SUCCESSFULLY " + m.memberID)
      }
      (response.json \ "members").as[List[JsObject]]
    }
  }

  def saveMemberData(json: List[JsObject]) = {
    println(
      json.headOption.getOrElse("nothing")
    )
  }

  def ward = Action.async { request =>
    LdsSession.getToken(request).fold(
      Future.successful(Unauthorized(oauth2.NOT_LOGGED_IN))
    ) { authToken =>
      Future {
        getMemberData(authToken, 518743, 189219)
          .map { data =>
          println(data.length)
          saveMemberData(data)
        }
        Ok("")
      }
    }
  }

}

case class CaseClass()

object CaseClass {
  implicit val format = {

  }
}

object LdsProfileJson {
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