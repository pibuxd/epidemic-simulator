package simulator.disease

class BasicDisease(
  name: String = "Basic Disease",
  base_infection_prob: Double = 0.3,
  recovery: Int = 14,
  mortality: Double = 0.01,
  incubation: Int = 3
) extends Disease {
  
  override def get_name(): String = name
  override def get_base_infection_probability(): Double = base_infection_prob
  override def get_recovery_time(): Int = recovery
  override def get_mortality_rate(): Double = mortality
  override def get_incubation_period(): Int = incubation
  override def is_symptomatic_infectious(): Boolean = false 
}