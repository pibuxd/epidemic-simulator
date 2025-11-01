package simulator.fields

import simulator.Board
import simulator.people.*

import scala.util.Try

class BasicField(x: Int, y: Int, neighbour_layers: Int) extends Field {
  override protected val position: (Int, Int) = (x, y)

  override def clear(): Unit = {
    inhabitants = Seq.empty
    for (i <- 0 until neighbour_layers) infected_number{i} = 0
  }
  
  override def check_in(person: Person): Unit = {
    if (person.infected) infected_number{0} += 1
    inhabitants = inhabitants:+ person
  }

//  override def infect_neighbours(): Unit = ???
//
//  override def infect_inhabitants(): Unit 
    //...
//  }

  override protected val infected_number: Array[Int] = Array.ofDim(neighbour_layers+1)
  override val neighbours: Array[IndexedSeq[Field]] = Array.ofDim(neighbour_layers+1)

  override def calculate_neighbours(board: Board, layer: Int): Unit = {
    val nearest_neighbours: Seq[(Int, Int)] = Seq(
      (0, 1),
      (0, -1),
      (1, 0),
      (-1, 0),
      if (x%2 == 0) (-1, -1) else (-1,1),
      if (x%2 == 0) (1, -1) else (1,1)
    )
    layer match {
      case 0 => neighbours{0} = IndexedSeq(this)
      case 1 => neighbours{1} = nearest_neighbours.flatMap((i,j) => Try(board.fields{x+i}{y+j}).toOption).toIndexedSeq
      case _ => neighbours{layer} = (neighbours{layer - 1}.flatMap(n => n.neighbours {1}).toSet -- neighbours{0} -- neighbours{1}).toIndexedSeq
    }
  }

}
