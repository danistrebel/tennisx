package io.github.danistrebel.tennisgraph

import java.io.{BufferedWriter, File, FileWriter}

object GraphVisualizer {

  def writeGraph[T](vertices: Array[(T, Double)], edges: Array[(T, T)], topKRanksOnly: Option[Int]) = {
    val file = new File("./index.html")
    val bw = new BufferedWriter(new FileWriter(file))
    val (filteredVertices, filteredEdges) = topKRanksOnly match {
      case Some(k) => {
        val kVertices = vertices.sortBy(_._2).takeRight(k)
        val kVertexIds = kVertices.map(_._1).toSet
        val kEdges = edges.filter(e => kVertexIds.contains(e._1) && kVertexIds.contains(e._2))
        (kVertices, kEdges)
      }
      case None => (vertices, edges)
    }
    bw.write(htmlBoilerplate(filteredVertices, filteredEdges))
    bw.close()
  }

  private[this] def htmlBoilerplate[T](vertices: Array[(T, Double)], edges: Array[(T, T)]) =
    s"""
      |<!DOCTYPE html>
      |<meta charset="utf-8">
      |<style>
      |
      |.links line {
      |  stroke: #999;
      |  stroke-opacity: 0.6;
      |}
      |
      |.nodes circle {
      |  stroke: #fff;
      |  stroke-width: 1.5px;
      |}
      |
      |</style>
      |<svg width="1024" height="768"></svg>
      |<script src="https://d3js.org/d3.v4.min.js"></script>
      |<script>
      |
      |var svg = d3.select("svg"),
      |    width = + svg.attr("width"),
      |    height = +svg.attr("height");
      |
      |var color = d3.scalePow().domain([0,${vertices.map(_._2).max}]).range(["yellow","red"]);
      |
      |var simulation = d3.forceSimulation()
      |    .force("link", d3.forceLink().id(function(d) { return d.id; }))
      |    .force("charge", d3.forceManyBody().strength(-250))
      |    .force("center", d3.forceCenter(width / 2, height / 2));
      |
      |var graph = {
      |  "nodes": [${vertices.map({case (v, rank) => s"""{"id": "$v", "rank": $rank}""" }).mkString(",\n")}],
      |  "links": [${
      edges
        .groupBy(x => x)
        .mapValues(_.length)
        .map({ case ((start, end), count) => s"""{"source": "$start", "target": "$end", "value": ${count}}"""}).mkString(",\n")}]
      |}
      |
      |
      |  var link = svg.append("g")
      |      .attr("class", "links")
      |    .selectAll("line")
      |    .data(graph.links)
      |    .enter().append("line")
      |      .attr("stroke-width", function(d) { return Math.sqrt(d.value); });
      |
      |  var node = svg.append("g")
      |      .attr("class", "nodes")
      |    .selectAll("circle")
      |    .data(graph.nodes)
      |    .enter().append("circle")
      |      .attr("r", function(d) { return 5; })
      |      .attr("fill", function(d) { return color(d.rank); })
      |      .call(d3.drag()
      |          .on("start", dragstarted)
      |          .on("drag", dragged)
      |          .on("end", dragended));
      |
      |  node.append("title")
      |      .text(function(d) { return d.id; });
      |
      |  simulation
      |      .nodes(graph.nodes)
      |      .on("tick", ticked);
      |
      |  simulation.force("link")
      |      .links(graph.links);
      |
      |  function ticked() {
      |    link
      |        .attr("x1", function(d) { return d.source.x; })
      |        .attr("y1", function(d) { return d.source.y; })
      |        .attr("x2", function(d) { return d.target.x; })
      |        .attr("y2", function(d) { return d.target.y; });
      |
      |    node
      |        .attr("cx", function(d) { return d.x; })
      |        .attr("cy", function(d) { return d.y; });
      |  }
      |
      |function dragstarted(d) {
      |  if (!d3.event.active) simulation.alphaTarget(0.3).restart();
      |  d.fx = d.x;
      |  d.fy = d.y;
      |}
      |
      |function dragged(d) {
      |  d.fx = d3.event.x;
      |  d.fy = d3.event.y;
      |}
      |
      |function dragended(d) {
      |  if (!d3.event.active) simulation.alphaTarget(0);
      |  d.fx = null;
      |  d.fy = null;
      |}
      |
      |</script>
      |    """.stripMargin
}
