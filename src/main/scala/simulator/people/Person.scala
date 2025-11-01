package simulator.people

import simulator.Board

trait Person {
  protected var position: (Int, Int) = (0, 0)
  var infected: Boolean = false
  
  def get_position(): (Int, Int) = position.copy()
  def infect_yourself(chance: Int): Unit
  def make_step(): Unit

}
