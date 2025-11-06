package simulator

import simulator.disease.Disease
import simulator.fields.Field
import scala.util.Try

class InfectionMap(board: Board, disease: Disease) {
  private val width = board.fields.length
  private val height = board.fields(0).length
  
  // N x N array storing infection probability for each field
  private val probabilities: Array[Array[Double]] = Array.ofDim[Double](width, height)
  
  // Calculate infection probabilities for all fields based on infected inhabitants
  def calculate(): Unit = {
    for {
      x <- 0 until width
      y <- 0 until height
    } {
      probabilities(x)(y) = 0.0
    }
    
    for {
      x <- 0 until width
      y <- 0 until height
    } {
      val field = board.fields(x)(y)
      if (field.local_infected_count > 0) {
        spreadInfectionFrom(x, y, field.local_infected_count)
      }
    }
  }
  
  private def spreadInfectionFrom(sourceX: Int, sourceY: Int, infectedCount: Int): Unit = {
    val maxDistance = disease.get_max_infection_distance()
    
    var distance = 0
    var currentLayer = Set((sourceX, sourceY))
    var visited = Set.empty[(Int, Int)]
    
    while (distance <= maxDistance && disease.get_infection_probability_by_layer(distance) > 0) {
      currentLayer.foreach { case (x, y) =>
        val probability = disease.calculate_infection_chance(infectedCount, distance)
        
        // P(A or B) = 1 - (1-P(A)) * (1-P(B))
        val currentProb = probabilities(x)(y)
        probabilities(x)(y) = 1.0 - (1.0 - currentProb) * (1.0 - probability)
      }
      
      // Prepare next layer
      visited = visited ++ currentLayer
      val nextLayer = currentLayer.flatMap { case (x, y) =>
        getDirectNeighbours(x, y)
      } -- visited
      
      currentLayer = nextLayer
      distance += 1
    }
  }
  
  // HEX GRID NEIGHBOURS
  private def getDirectNeighbours(x: Int, y: Int): Seq[(Int, Int)] = {
    val offsets = Seq(
      (0, 1),
      (0, -1),
      (1, 0),
      (-1, 0),
      if (x % 2 == 0) (-1, -1) else (-1, 1),
      if (x % 2 == 0) (1, -1) else (1, 1)
    )
    
    offsets.flatMap { case (dx, dy) =>
      val nx = x + dx
      val ny = y + dy
      if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
        Some((nx, ny))
      } else {
        None
      }
    }
  }
  
  def getProbability(x: Int, y: Int): Double = probabilities(x)(y)
}
