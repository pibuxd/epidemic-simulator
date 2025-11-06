package simulator

import simulator.people.*
import simulator.disease.*

object Main{
  def main(args: Array[String]): Unit = {

    val BOARD_WIDTH: Int = 10
    val BOARD_HEIGHT: Int = 10
    val TURNS = 20

    // Try with more contagious disease
    val disease: Disease = BasicDisease(base_infection_prob = 0.5)  // 50% infection rate

    val start = System.currentTimeMillis()
    val board: Board = Board(BOARD_WIDTH, BOARD_HEIGHT)
    val after = System.currentTimeMillis()
    val time = after - start
    println(s"Board generation costed us $time milliseconds")

    val people: Seq[Person] = ((1 to 9) map (i => BasicPerson(i, i, false, board)))
                                            ++ Some(BasicPerson(9, 9, true, board))

    def movement_turn(): Unit = {
      board.fields.flatten.foreach(field => field.clear())
      people.foreach(person => person.make_step())
    }

    def infection_turn(): Unit = {
      val infectionMap = InfectionMap(board, disease)
      infectionMap.calculate()
      
      for {
        x <- 0 until BOARD_WIDTH
        y <- 0 until BOARD_HEIGHT
      } {
        val field = board.fields(x)(y)
        val probability = infectionMap.getProbability(x, y)
        field.infect_inhabitants(probability)
      }
    }

    println(s"Starting simulation with ${people.size} people, 1 infected")
    println(s"Disease: ${disease.get_name()}, base probability: ${disease.get_base_infection_probability()}")
    println(s"Max infection distance: ${disease.get_max_infection_distance()} layers\n")
    
    for (i <- 1 to TURNS) {
      println(s"Turn $i:")
      movement_turn()
      infection_turn()
      
      val infected_count = people.count(_.infected)
      println(s"  Infected people: $infected_count / ${people.size}")
    }
    
    println("\n=== Simulation Complete ===")
    val final_infected = people.count(_.infected)
    println(s"Final infected: $final_infected / ${people.size}")
  }
}