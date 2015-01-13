package controllers


import play.api.Play.current
import play.api.http.Writeable
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

object Hymns extends Controller {

  def hymn(page: Int) = Action {
    val json: JsValue = JsObject(Seq(
      "page" -> JsNumber(page)
    ))
    Ok(json)
  }

  def convert = Action.async {
    val hymnsSourceUrl = "http://broadcast3.lds.org/crowdsource/Mobile/LDSMusic/Staging/Collections/Hymns-EN/55/Collection.json"

    WS.url(hymnsSourceUrl).get().map { response =>
      val items = response.json \ "items"
      val itemList = items.as[List[JsObject]]
      Ok(Json.toJson(itemList.map(hymn => {
        JsObject(Seq(
          "id" -> hymn \ "id",
          "number" -> JsNumber((hymn \ "number").as[JsString].value.toInt),
          "name" -> hymn \ "name"
        ))
      })))
    }
  }

}
