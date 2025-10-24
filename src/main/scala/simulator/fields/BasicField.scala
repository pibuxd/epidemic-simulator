package simulator.fields

import simulator.Board

import scala.util.Try

class BasicField(x: Int, y: Int, neighbour_layers: Int) extends Field {
  override protected val position: (Int, Int) = (x, y)
//
//  override def check_in(person: Person): Unit = ???
//
//  override def infect_neighbours(): Unit = ???
//
//  override def infect_inhabitants(): Unit = ???
//
//  override protected val infected_number: Array[Int] = ???
//  override protected val inhabitants: Seq[Person] = ???
//  override protected val neighbours: Array[Seq[Field]] = ???
  override val neighbours: Array[Set[Field]] = Array.ofDim(neighbour_layers+1)

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
      case 0 => neighbours{0} = Set(this)
      case 1 => neighbours{1} = nearest_neighbours.flatMap((i,j) => Try(board.fields{x+i}{y+j}).toOption).toSet
      case _ => neighbours{layer} = neighbours {layer - 1}.flatMap(n => n.neighbours {1}) -- neighbours{0} -- neighbours{1}
    }
  }

}
