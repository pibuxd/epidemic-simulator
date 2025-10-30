package simulator

import simulator.people.*

object Main{
  def main(args: Array[String]): Unit = {

    // Simulation Preparation
    val start = System.currentTimeMillis()
    val board: Board = Board(10,10,3)
    val after = System.currentTimeMillis()
    val time = after - start
    println(s"Board generation costed us $time miliseconds")

    // println(board.fields{1}{0}.neighbours{1}.size)
    val people: Seq[Person] = ((1 to 0) map (i => BasicPerson(i, i, false, board)))
                                            ++ Some(BasicPerson(9, 9, true, board))

    // Turns
    def movement_turn(): Unit = {
      board.fields.flatten.foreach(field => field.clear())
      people.foreach(person => person.make_step())
      // people.foreach(person => println(person.get_position()))
    }

    // Main loop
    for (i <- 1 to 10) {
      movement_turn()
//      println(i)
//      board.fields.flatten.foreach(field => field.inhabitants.foreach(inhab => println(inhab.get_position())))
    }
  }
}