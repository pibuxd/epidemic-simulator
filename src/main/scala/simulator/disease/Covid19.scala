package simulator.disease

class Covid19 extends Disease {
  override def get_name(): String = "COVID-19"
  override def get_base_infection_probability(): Double = 0.15
  override def get_recovery_time(): Int = 14
  override def get_mortality_rate(): Double = 0.02
  override def get_incubation_period(): Int = 5
  override def is_symptomatic_infectious(): Boolean = false
}