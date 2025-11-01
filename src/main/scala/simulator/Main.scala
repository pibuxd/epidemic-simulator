package simulator

import simulator.people.*

object Main{
  def main(args: Array[String]): Unit = {

    // Constants
    val BOARD_WIDTH: Int = 10
    val BOARD_HEIGHT: Int = 10
    val LAYERS: Int = 3
    val TURNS = 10

    // Simulation Preparation
    val start = System.currentTimeMillis()
    val board: Board = Board(BOARD_WIDTH, BOARD_HEIGHT, LAYERS)
    val after = System.currentTimeMillis()
    val time = after - start
    println(s"Board generation costed us $time milliseconds")

    // println(board.fields{1}{0}.neighbours{1}.size)
    val people: Seq[Person] = ((1 to 0) map (i => BasicPerson(i, i, false, board)))
                                            ++ Some(BasicPerson(9, 9, true, board))

    // Turns
    def movement_turn(): Unit = {
      board.fields.flatten.foreach(field => field.clear())
      people.foreach(person => person.make_step())
      // people.foreach(person => println(person.get_position()))
    }

    def infection_spread_turn(): Unit = {
      board.fields.flatten.foreach(field => field.infect_neighbours())
    }

    // Main loop
    for (i <- 1 to TURNS) {
      movement_turn()
//      println(i)
//      board.fields.flatten.foreach(field => field.inhabitants.foreach(inhab => println(inhab.get_position())))
      infection_spread_turn()
    }
  }
}