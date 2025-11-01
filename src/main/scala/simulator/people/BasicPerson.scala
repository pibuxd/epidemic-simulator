package simulator.people

import simulator.Board

import scala.util.Random

class BasicPerson(start_x: Int, start_y: Int, start_infected: Boolean, board: Board) extends Person {

  position = (start_x, start_y)
  infected = start_infected

  private def current_field() = {
    board.fields{position._1}{position._2}
  }

  current_field().check_in(this)

  override def infect_yourself(chance: Int): Unit = {
    if (!infected && Random().nextInt(100) < chance) infected = true
  }

  override def make_step(): Unit = {
    val options = current_field().neighbours{1}
    position = options{Random.nextInt(options.size)}.get_position()
    current_field().check_in(this)
  }

}
