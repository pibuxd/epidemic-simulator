package simulator.disease

import scala.util.Random

trait Disease {
  def get_name(): String
  def get_base_infection_probability(): Double // base probability of infection upon contact (as a percentage)
  def get_recovery_time(): Int // in days
  def get_mortality_rate(): Double // as a percentage
  def get_incubation_period(): Int // in days
  def is_symptomatic_infectious(): Boolean // whether symptomatic (okres bezobjawowy) individuals can spread the disease
  
  def get_max_infection_distance(): Int = 3
  
  // Infection probability by distance with case matching
  def get_infection_probability_by_layer(layer: Int): Double = {
    if (layer > get_max_infection_distance()) return 0.0
    
    val base = get_base_infection_probability()
    base * (layer match {
      case 0 => 1.0     // same field - 100%
      case 1 => 0.5     // neighbors - 50%
      case 2 => 0.33    // 2 away - 33%
      case 3 => 0.25    // 3 away - 25%
      case _ => 1.0 / (layer + 1.0)
    })
  }
  
  // 1 - (1 - p)^n where p is probability per infected person, n is count
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