package simulator.disease

import scala.util.Random

trait Disease {
  val get_name: String
  val get_base_infection_probability: Double
  val get_recovery_time: Int
  val get_mortality_rate: Double
  val get_incubation_period: Int
  val is_symptomatic_infectious: Boolean

  val get_max_infection_distance: Int = 3
  
  def get_infection_probability_by_layer(layer: Int): Double = {
    if (layer > get_max_infection_distance) return 0.0
    
    val base = get_base_infection_probability
    base * (layer match {
      case 0 => 1.0
      case 1 => 0.5
      case 2 => 0.33
      case 3 => 0.25
      case _ => 1.0 / (layer + 1.0)
    })
  }
  
  // Combined infection probability from n sources: 1 - (1-p)^n
  def calculate_infection_chance(infected_count: Int, layer: Int): Double = {
    if (infected_count == 0) return 0.0
    val base_prob = get_infection_probability_by_layer(layer)
    1.0 - Math.pow(1.0 - base_prob, infected_count)
  }
  
  def should_get_infected(infected_count: Int, layer: Int): Boolean = {
    val chance = calculate_infection_chance(infected_count, layer)
    Random.nextDouble() < chance
  }
}