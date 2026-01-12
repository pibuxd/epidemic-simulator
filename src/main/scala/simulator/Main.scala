package simulator

import simulator.people._
import simulator.disease._
import simulator.fields._
import com.typesafe.config.ConfigFactory

object Main{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    
    val BOARD_WIDTH = config.getInt("simulator.board.width")
    val BOARD_HEIGHT = config.getInt("simulator.board.height")
    val LAYERS = config.getInt("simulator.board.layers")
    val TURNS = config.getInt("simulator.turns")
    val TOTAL_PEOPLE = config.getInt("simulator.population.total")
    val INITIAL_INFECTED = config.getInt("simulator.population.initial_infected")
    val BASE_INFECTION_PROB = config.getDouble("simulator.disease.base_infection_prob")

    val disease: Disease = new BasicDisease(base_infection_prob = BASE_INFECTION_PROB)

    val start = System.currentTimeMillis()
    val board: Board = new Board(BOARD_WIDTH, BOARD_HEIGHT, LAYERS)
    val after = System.currentTimeMillis()
    val time = after - start
    println(s"Board generation completed in $time milliseconds\n")

    val healthyCount = TOTAL_PEOPLE - INITIAL_INFECTED
    val people: Seq[Person] = (0 until healthyCount).map(i => new BasicPerson(i % BOARD_WIDTH, i / BOARD_WIDTH, false, board)).toSeq ++
                              (0 until INITIAL_INFECTED).map(_ => new BasicPerson(BOARD_WIDTH - 1, BOARD_HEIGHT - 1, true, board))

    def movement_turn(): Unit = {
      board.fields.flatten.foreach(field => field.clear())
      people.foreach(person => person.make_step())
    }

    def infection_turn(): Unit = {
      val infectionMap = new InfectionMap(board, disease)
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

    println(s"Starting simulation with ${people.size} people, $INITIAL_INFECTED infected")
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
