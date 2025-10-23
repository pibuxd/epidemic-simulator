package simulator

import simulator.fields.*

class Board(height: Int, width: Int) {
  val fields: Array[Array[Field]] = Array.ofDim(height, width)
}