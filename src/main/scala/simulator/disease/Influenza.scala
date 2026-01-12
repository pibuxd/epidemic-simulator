package simulator.disease

class Influenza extends Disease {
  override def get_name(): String = "Influenza"
  override def get_base_infection_probability(): Double = 0.25
  override def get_recovery_time(): Int = 7
  override def get_mortality_rate(): Double = 0.001
  override def get_incubation_period(): Int = 2
  override def is_symptomatic_infectious(): Boolean = true
}
