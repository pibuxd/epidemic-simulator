package simulator.fields

import simulator.Board
import simulator.people.*
import simulator.disease.Disease

class BasicField(x: Int, y: Int) extends Field {
  override protected val position: (Int, Int) = (x, y)
  
  var local_infected_count: Int = 0  
  
  override def clear(): Unit = {
    inhabitants = Seq.empty
    local_infected_count = 0
  }
  
  override def check_in(person: Person): Unit = {
    if (person.infected) local_infected_count += 1
    inhabitants = inhabitants :+ person
  }
  
  override def infect_inhabitants(infection_probability: Double): Unit = {
    inhabitants.foreach { person =>
      if (!person.infected) {
        if (scala.util.Random.nextDouble() < infection_probability) {
          person.infected = true
        }
      }
    }
  }
  
  def get_direct_neighbours(board: Board): Seq[Field] = {
    val offsets = Seq(
      (0, 1),
      (0, -1),
      (1, 0),
      (-1, 0),
      if (x % 2 == 0) (-1, -1) else (-1, 1),
      if (x % 2 == 0) (1, -1) else (1, 1)
    )
    
    offsets.flatMap { case (dx, dy) =>
      val nx = x + dx
      val ny = y + dy
      if (nx >= 0 && nx < board.fields.length && ny >= 0 && ny < board.fields(0).length) {
        Some(board.fields(nx)(ny))
      } else {
        None
      }
    }
  }
}
