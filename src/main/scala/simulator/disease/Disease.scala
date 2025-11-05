package simulator.disease

trait Disease {
  def get_name(): String
  def get_base_infection_probability(): Double // base probability of infection upon contact (as a percentage)
  def get_recovery_time(): Int // in days
  def get_mortality_rate(): Double // as a percentage
  def get_incubation_period(): Int // in days
  def is_symptomatic_infectious(): Boolean // whether symptomatic (okres bezobjawowy) individuals can spread the disease
}