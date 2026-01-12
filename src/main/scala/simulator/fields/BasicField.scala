package simulator.fields

import simulator.Board
import simulator.people._
import simulator.disease.Disease
import scala.util.Try

class BasicField(x: Int, y: Int, neighbour_layers: Int) extends Field {
  override protected val position: (Int, Int) = (x, y)
  
  var local_infected_count: Int = 0
  override val neighbours: Array[IndexedSeq[Field]] = Array.ofDim(neighbour_layers + 1)
  
  override def clear(): Unit = {
    inhabitants = Seq.empty
    local_infected_count = 0
  }
  
  override def check_in(person: Person): Unit = {
    if (person.infected) local_infected_count += 1
    inhabitants = inhabitants :+ person
  }
  
  override def infect_inhabitants(infection_probability: Double): Unit = {
    val roll = scala.util.Random.nextDouble()
    val shouldInfect = roll < infection_probability
    
    if (shouldInfect) {
      inhabitants.foreach { person =>
        if (!person.infected) {
          person.infected = true
        }
      }
    }
  }
  
  override def calculate_neighbours(board: Board, layer: Int): Unit = {
    layer match {
      case 0 => 
        neighbours(0) = IndexedSeq(this)
      case 1 => 
        neighbours(1) = get_direct_neighbours(board)
      case _ => 
        neighbours(layer) = (neighbours(layer - 1)
          .flatMap(_.neighbours(1))
          .toSet -- neighbours(0) -- neighbours(1))
          .toIndexedSeq
    }
  }
  
  def get_direct_neighbours(board: Board): IndexedSeq[Field] = {
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
      Try(board.fields(nx)(ny)).toOption
    }.toIndexedSeq
  }
}
