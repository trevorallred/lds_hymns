package controllers

import play.api.mvc._
import play.api.libs.json._

object Hymns extends Controller {

  def hymn(page: Int) = Action {
    val json: JsValue = JsObject(Seq(
      "page" -> JsNumber(page)
    ))
    Ok(json)
  }

}
