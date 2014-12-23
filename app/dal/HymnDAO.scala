package dal

import models.Person

import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class HymnDAO(_dal: SlickDAL) extends BaseDAO(_dal) {
  import dal.profile.simple._
  import dal.People

}
