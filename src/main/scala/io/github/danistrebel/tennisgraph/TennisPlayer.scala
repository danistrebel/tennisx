package io.github.danistrebel.tennisgraph

case class TennisPlayer(id: Long, name: String, country: String) {

  override def toString(): String = s"${name} (${id})"

}
