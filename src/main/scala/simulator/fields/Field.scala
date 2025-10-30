package simulator.fields

import simulator.Board
import simulator.people.*

trait Field {
  def clear(): Unit
  def check_in(person: Person): Unit
//  def infect_neighbours(): Unit
//  def infect_inhabitants(): Unit 
  def get_position(): (Int, Int) = {
    position
  }

  def calculate_neighbours(board: Board, layer: Int): Unit

  protected val infected_number: Array[Int]
  var inhabitants: Seq[Person] = Seq.empty
  val neighbours: Array[IndexedSeq[Field]]
  protected val position: (Int, Int)
}