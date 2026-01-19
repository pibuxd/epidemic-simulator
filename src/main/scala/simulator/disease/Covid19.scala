package simulator.disease

class Covid19 extends Disease {
  override val get_name: String = "COVID-19"
  override val get_base_infection_probability: Double = 0.15
  override val get_recovery_time: Int = 14
  override val get_mortality_rate: Double = 0.02
  override val get_incubation_period: Int = 5
  override val is_symptomatic_infectious: Boolean = false
}