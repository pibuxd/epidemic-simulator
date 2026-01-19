package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import simulator.fields._

class BoardSpec extends AnyFlatSpec with Matchers {

  "A Board" should "initialize correct number of fields" in {
    val width = 10
    val height = 10
    val board = new Board(width, height, 1)

    board.fields.length should be (width)
    board.fields(0).length should be (height)
  }

  it should "calculate neighbours for a field" in {
    val board = new Board(5, 5, 1)
    val centerField = board.fields(2)(2)
    val neighbours = centerField.neighbours(1)
    
    neighbours should not be empty
    neighbours.length should be (6)
  }
}