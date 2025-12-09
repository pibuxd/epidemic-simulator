package simulator

import simulator.fields._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Board(width: Int, height: Int, neighbour_layers: Int) {

  val fields: Seq[Seq[BasicField]] = Seq.tabulate(width, height)(
    (x, y) => new BasicField(x, y, neighbour_layers)
  )

  println(s"Preprocessing neighbours for $width x $height board with $neighbour_layers layers...")
  val preprocessStart = System.currentTimeMillis()
  
  (0 to neighbour_layers).foreach { layer =>
    fields.flatten.foreach { field =>
      field.calculate_neighbours(this, layer)
    }
  }
  
  val preprocessEnd = System.currentTimeMillis()
  println(s"Preprocessing completed in ${preprocessEnd - preprocessStart} ms")
}
