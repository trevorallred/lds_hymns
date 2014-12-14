package dal

object DAO {
  private val dal = app.Global.dal

  val peopleDAO = new PeopleDAO(dal)
}
