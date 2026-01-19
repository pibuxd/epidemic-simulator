package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import simulator.fields._
import simulator.people._

class MovementSpec extends AnyFlatSpec with Matchers {

  "A BasicPerson" should "move to a valid neighbour field" in {
    val width = 10
    val height = 10
    val board = new Board(width, height, 1)
    
    val person = new BasicPerson(0, 0, false, board)
    val startPos = person.get_position()
    
    person.make_step()
    val endPos = person.get_position()

    endPos._1 should be >= 0
    endPos._1 should be < width
    endPos._2 should be >= 0
    endPos._2 should be < height
  }
}