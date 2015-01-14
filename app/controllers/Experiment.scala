package controllers

import app.Global
import controllers.Application._
import dal.DAO
import models.Stake
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

object Experiment {
  def index = Action.async {
    DAO.stakeDAO.save(new Stake(unitNo = scala.util.Random.nextInt(100000), name = "123", None))
    // Iterate through all coffees and output them
    DAO.stakeDAO.all map { stakes =>
      stakes.foreach { stake =>
        println(stake)
      }
      Ok("Just Experimenting")
    }
  }
}
