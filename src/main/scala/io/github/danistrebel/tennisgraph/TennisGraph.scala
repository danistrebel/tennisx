package io.github.danistrebel.tennisgraph

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Toy example of using the ATP tournament data to generate a graph of players and
  * using page rank to infer relative strengths of tennis players.
  */
object TennisGraph extends App {

  val conf = new SparkConf().setAppName("TennisGraph").setMaster("local")
  val sc = new SparkContext(conf)
  LogManager.getRootLogger.setLevel(Level.WARN)

  // Pre-process Input
  val textRDD = sc.textFile("src/main/resources/atp_matches*.csv")
  val header = textRDD.first()

  val tournamentsRDD = textRDD
      .filter(l => l != header && l.trim.length > 0)
      .map(TournamentRecord.parseRecord).cache()

  // Compute straight forward prediction accuracy based on ATP Ranking
  val underdogWins = tournamentsRDD.filter(tournament => tournament.loserRank.getOrElse(0) < tournament.winnerRank.getOrElse(0))
  val underdogsLost = tournamentsRDD.filter(tournament => tournament.loserRank.getOrElse(0) > tournament.winnerRank.getOrElse(0))

  println(s"Underdogs won ${underdogWins.count()} out of ${tournamentsRDD.count()} matches")

  //Build the graph
  val playerVertices = tournamentsRDD
    .flatMap(t => List(
        TennisPlayer(t.winnerId, t.winnerName, t.winnerIoc),
        TennisPlayer(t.loserId, t.loserName, t.loserIoc)
    ))
    .distinct()
    .map(p => (p.id, p))
    .cache()

  val gameEdges = tournamentsRDD
    .map(t => Edge(t.loserId, t.winnerId, t.score))
    .cache()

  val graph = Graph(playerVertices, gameEdges)

  //  Sanity check
  graph.triplets
    .map(tp => s"${tp.dstAttr} won against ${tp.srcAttr} ${tp.attr}")
    .take(10)
    .foreach(s => println(s))

  // Page rank on Players
  val ranks = graph.pageRank(0.0001).vertices

  val playerRanks = ranks
    .join(playerVertices).map({
      case (_, (rank, player)) => (rank, player)
    })
    .sortBy(_._1, false)
    .collect()

  playerRanks.foreach({ case (rank, player) => println(s"$rank: $player")})

  // Visualization of the Structure
  GraphVisualizer.writeGraph(
    playerRanks.map({ case (rank, player) => (player.id, rank)}),
    tournamentsRDD.map(t => (t.winnerId, t.loserId)).collect(),
    Some(100)
  )

  sc.stop()

}
