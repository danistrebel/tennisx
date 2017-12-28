package io.github.danistrebel.tennisgraph

case class TournamentRecord(
                           tournamentId: String,
                           tournamentLocation: String,
                           surface: Option[String],
                           drawSize: Int,
                           tournamentLevel: String,
                           tournamentDate: String,
                           matchNumber: Int,
                           winnerId: Long,
                           winnerSeed: Option[Int],
                           winnerEntry: Option[String],
                           winnerName: String,
                           winnerHand: String,
                           winnerHt: Option[Int],
                           winnerIoc: String,
                           winnerAge: Option[Double],
                           winnerRank: Option[Int],
                           winnerRankPoints: Option[Int],
                           loserId: Long,
                           loserSeed: Option[Int],
                           loserEntry: Option[String],
                           loserName: String,
                           loserHand: String,
                           loserHt: Option[Int],
                           loserIoc: String,
                           loserAge: Option[Double],
                           loserRank: Option[Int],
                           loserRankPoints: Option[Int],
                           score: String,
                           bestOf: Int,
                           round: String,
                           minutes: Option[Int],
                           winnerAce: Option[Int],
                           winnerDf: Option[Int],
                           winnerSvpt: Option[Int],
                           winner1stIn: Option[Int],
                           winner1stWon: Option[Int],
                           winner2ndWon: Option[Int],
                           winnerSvGms: Option[Int],
                           winnerBpSaved: Option[Int],
                           winnerBpFaced: Option[Int],
                           loserAce: Option[Int],
                           loserDf: Option[Int],
                           loserSvpt: Option[Int],
                           loser1stIn: Option[Int],
                           loser1stWon: Option[Int],
                           loser2ndWon: Option[Int],
                           loserSvGms: Option[Int],
                           loserBpSaved: Option[Int],
                           loserBpFaced: Option[Int]
                           ) {
  override def toString: String = {
    s"""ATP Tournament ${tournamentLocation}
       |Game: #${matchNumber}
       |
       |Winner: ${winnerName}
       |Loser: ${loserName}
       |""".stripMargin
  }
}

object TournamentRecord {

  private def optionalInt(s: Array[String], i: Int): Option[Int] =
    if (s.length <= i || s(i).trim.isEmpty) None else Some(s(i).toInt)


  def parseRecord(s: String): TournamentRecord = {
    val l = s.split(",")

    var tournamentRecord: TournamentRecord = null

    try {

    tournamentRecord = TournamentRecord(
      l(0),
      l(1),
      if (l(2).isEmpty) None else Some(l(2)),
      l(3).toInt,
      l(4),
      l(5),
      l(6).toInt,
      l(7).toLong,
      optionalInt(l, 8),
      if (l(9).isEmpty) None else Some(l(9)),
      l(10),
      l(11),
      optionalInt(l, 12),
      l(13),
      if (l(14).isEmpty) None else Some(l(14).toDouble),
      optionalInt(l,15),
      optionalInt(l,16),
      l(17).toLong,
      optionalInt(l, 18),
      if (l(19).isEmpty) None else Some(l(19)),
      l(20),
      l(21),
      optionalInt(l, 22),
      l(23),
      if (l(24).isEmpty) None else Some(l(24).toDouble),
      optionalInt(l,25),
      optionalInt(l,26),
      l(27),
      l(28).toInt,
      l(29),
      optionalInt(l,30),
      optionalInt(l, 31),
      optionalInt(l, 32),
      optionalInt(l, 33),
      optionalInt(l, 34),
      optionalInt(l, 35),
      optionalInt(l, 36),
      optionalInt(l, 37),
      optionalInt(l, 38),
      optionalInt(l, 39),
      optionalInt(l, 40),
      optionalInt(l, 41),
      optionalInt(l, 42),
      optionalInt(l, 43),
      optionalInt(l, 44),
      optionalInt(l, 45),
      optionalInt(l, 46),
      optionalInt(l, 47),
      optionalInt(l, 48)
    )
    } catch {
      case e: Exception => {
        System.err.println(e.toString)
        System.err.println("Error reading " + l.toList.toString)
      }
    }

    tournamentRecord

  }
}
