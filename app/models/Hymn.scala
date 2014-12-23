package models

case class Hymn(
                 id: Int,
                 title: String,
                 firstLine: String,
                 minTempo: Option[Int],
                 maxTempo: Option[Int]
                 ) {
  def firstLineEqualsTitle = {
    title == firstLine
  }
}
