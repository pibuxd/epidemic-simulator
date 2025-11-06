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
  
  def get_infection_probability_by_layer(layer: Int): Double = {
    if (layer > get_max_infection_distance()) return 0.0 // too far away
    layer match {
      case 0 => get_base_infection_probability() // same field - full probability
      case 1 => get_base_infection_probability() * 0.5 // direct neighbors - 50%
      case 2 => get_base_infection_probability() * 0.2 // 2 layers away - 20%
      case _ => get_base_infection_probability() * 0.05 // further - 5%
    }
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