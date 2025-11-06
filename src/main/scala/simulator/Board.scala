package simulator

import simulator.fields.*

class Board(width: Int, height: Int) {

  val fields: Seq[Seq[BasicField]] = Seq.tabulate(width, height)(
    (x, y) => BasicField(x, y)
  )
}