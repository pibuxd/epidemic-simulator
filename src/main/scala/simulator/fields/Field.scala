package simulator.fields

import simulator.Board
import simulator.people._
import simulator.disease.Disease

trait Field {
  def clear(): Unit
  def check_in(person: Person): Unit
  def infect_inhabitants(infection_probability: Double): Unit
  def calculate_neighbours(board: Board, layer: Int): Unit
  def get_position(): (Int, Int) = {
    position
  }
  
  var local_infected_count: Int
  var inhabitants: Seq[Person] = Seq.empty
  val neighbours: Array[IndexedSeq[Field]]
  protected val position: (Int, Int)
}