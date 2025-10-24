package simulator.fields

import simulator.Board

trait Field {
//  def check_in(person: Person): Unit
//  def infect_neighbours(): Unit
//  def infect_inhabitants(): Unit
  def get_position(): (Int, Int) = {
    position
  }

  def calculate_neighbours(board: Board, layer: Int): Unit

//  protected val infected_number: Array[Int]
//  protected val inhabitants: Seq[Person]
  val neighbours: Array[Set[Field]]
  protected val position: (Int, Int)
}