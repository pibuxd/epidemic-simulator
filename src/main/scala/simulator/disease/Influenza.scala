package simulator.disease

class Influenza extends Disease {
  override val get_name: String = "Influenza"
  override val get_base_infection_probability: Double = 0.25
  override val get_recovery_time: Int = 7
  override val get_mortality_rate: Double = 0.001
  override val get_incubation_period: Int = 2
  override val is_symptomatic_infectious: Boolean = true
}
