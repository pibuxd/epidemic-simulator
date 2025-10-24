package simulator

import simulator.fields.*

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

class Board(width: Int, height: Int, neighbour_layers: Int) {

  val fields: Seq[Seq[BasicField]] = Seq.tabulate(width, height)(
    (x, y) => BasicField(x, y, neighbour_layers)
  )

  (0 to neighbour_layers).foreach { layer =>
    val tasks = fields.flatten.map { field =>
      Future(field.calculate_neighbours(this, layer))
    }
    Await.result(Future.sequence(tasks), Duration.Inf)
  }
}