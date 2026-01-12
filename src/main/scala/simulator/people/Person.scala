package simulator.people

import simulator.Board
import simulator.disease.Disease

trait Person {
  protected var position: (Int, Int) = (0, 0)
  var infected: Boolean = false
  var dead: Boolean = false
  var days_infected: Int = 0
  
  def get_position(): (Int, Int) = position.copy()
  def make_step(): Unit

  def tick(disease: Disease): Unit = {
    if (dead) return
    if (infected) {
      days_infected += 1
      if (scala.util.Random.nextDouble() < disease.get_mortality_rate()) {
        dead = true
        infected = false
      } else if (days_infected >= disease.get_recovery_time()) {
        infected = false
        days_infected = 0
      }
    }
  }
}
