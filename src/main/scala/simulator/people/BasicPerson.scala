package simulator.people

import simulator.Board

import scala.util.Random

class BasicPerson(start_x: Int, start_y: Int, start_infected: Boolean, board: Board) extends Person {

  position = (start_x, start_y)
  infected = start_infected

  private def current_field() = {
    board.fields(position._1)(position._2)
  }

  current_field().check_in(this)

  private var direction_index: Int = Random.nextInt(6)
  private var direction_duration: Int = 0

  private def change_direction(turn: Int): Unit = {
    direction_index = (direction_index + turn + 6) % 6
    direction_duration = Random.nextInt(3) + 1
  }

  override def make_step(): Unit = {
    // Normal change of direction: rotate 1 step left or right (60 degrees)
    if (direction_duration <= 0) {
      val turn = if (Random.nextBoolean()) 1 else -1
      change_direction(turn)
    }

    // Get current direction vector
    val is_even_col = position._1 % 2 == 0
    val (dx, dy) = direction_index match {
      case 0 => (0, -1)
      case 1 => if (is_even_col) (1, -1) else (1, 0)
      case 2 => if (is_even_col) (1, 0) else (1, 1)
      case 3 => (0, 1)
      case 4 => if (is_even_col) (-1, 0) else (-1, 1)
      case 5 => if (is_even_col) (-1, -1) else (-1, 0)
    }

    val options = current_field().neighbours(1)
    if (options.nonEmpty) {
      val target_x = position._1 + dx
      val target_y = position._2 + dy

      // Move to target (or as close as possible)
      val best_option = options.minBy(f => {
        val (fx, fy) = f.get_position()
        math.pow(fx - target_x, 2) + math.pow(fy - target_y, 2)
      })

      position = best_option.get_position()
      current_field().check_in(this)

      // Wall hit: rotate 2 steps left or right (120 degrees)
      if (position != (target_x, target_y)) {
        val bounce = if (Random.nextBoolean()) 2 else -2
        change_direction(bounce)
      } else {
        direction_duration -= 1
      }
    }
  }

}
