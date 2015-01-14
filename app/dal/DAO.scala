package dal

object DAO {
  private val dal = app.Global.dal

  val stakeDAO = new StakeDAO(dal)
  val wardDAO = new WardDAO(dal)
  val memberDAO = new MemberDAO(dal)
}
