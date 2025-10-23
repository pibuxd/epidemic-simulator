package simulator.fields

import simulator.people.*

trait Field {
  def check_in(person: Person): Unit
  def infect_neighbours(): Unit
  def infect_inhabitants(): Unit

  protected val infected_number: Array[Int]
  protected val inhabitants: Seq[Person]
  protected val neighbours: Array[Seq[Field]]
}