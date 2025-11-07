package simulator

import simulator.disease.Disease

class InfectionMap(board: Board, disease: Disease) {
  private val width = board.fields.length
  private val height = board.fields(0).length
  
  // N x N array storing infection probability for each field
  private val probabilities: Array[Array[Double]] = Array.ofDim[Double](width, height)
  
  // infection probabilities for all fields based on infected inhabitants
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
    val sourceField = board.fields(sourceX)(sourceY)
    
    // preprocessed neighbours for each distance
    for (distance <- 0 to maxDistance) {
      if (disease.get_infection_probability_by_layer(distance) > 0) {
        val neighboursAtDistance = sourceField.neighbours(distance)
        val probability = disease.calculate_infection_chance(infectedCount, distance)
        
        neighboursAtDistance.foreach { field =>
          val (x, y) = field.get_position()

          // P(A or B) = 1 - (1-P(A)) * (1-P(B))
          val currentProb = probabilities(x)(y)
          probabilities(x)(y) = 1.0 - (1.0 - currentProb) * (1.0 - probability)
        }
      }
    }
  }
  
  def getProbability(x: Int, y: Int): Double = probabilities(x)(y)
}
