package simulator.disease

class BasicDisease() extends Disease {
  
  override val get_name: String = "Basic Disease"
  override val get_base_infection_probability: Double = 0.3
  override val get_recovery_time: Int = 14
  override val get_mortality_rate: Double = 0.01
  override val get_incubation_period: Int = 3
  override val is_symptomatic_infectious: Boolean = false
}