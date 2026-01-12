package simulator.disease

class BasicDisease() extends Disease {
  
  override def get_name(): String = "Basic Disease"
  override def get_base_infection_probability(): Double = 0.3
  override def get_recovery_time(): Int = 14
  override def get_mortality_rate(): Double = 0.01
  override def get_incubation_period(): Int = 3
  override def is_symptomatic_infectious(): Boolean = false
}