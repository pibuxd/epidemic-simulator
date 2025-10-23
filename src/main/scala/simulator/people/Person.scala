package simulator.people

import simulator.Board

trait Person {
  def infect_yourself(chance: Int): Unit
  def get_position(): (Int, Int)
  def make_step(): Unit

  protected val position: (Int, Int)
  protected val board: Board
  protected val infected: Boolean
}
