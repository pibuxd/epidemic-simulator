package simulator

import simulator.people.*
import simulator.disease.*
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class Simulation() {
  private val config = ConfigFactory.load()

  private val BOARD_WIDTH = config.getInt("simulator.board.width")
  private val BOARD_HEIGHT = config.getInt("simulator.board.height")
  private val LAYERS = config.getInt("simulator.board.layers")
  val TURNS: Int = config.getInt("simulator.turns")
  private val TOTAL_PEOPLE = config.getInt("simulator.population.total")
  private val INITIAL_INFECTED = config.getInt("simulator.population.initial_infected")
  private val DISEASE_TYPE = config.getString("simulator.disease.type")

  val disease: Disease = Class.forName(s"simulator.disease.$DISEASE_TYPE")
    .getDeclaredConstructor()
    .newInstance()
    .asInstanceOf[Disease]

  private val start = System.currentTimeMillis()
  private val board: Board = new Board(BOARD_WIDTH, BOARD_HEIGHT, LAYERS)
  private val after = System.currentTimeMillis()
  private val time = after - start
  println(s"Board generation completed in $time milliseconds\n")
  val people: ArrayBuffer[Person] = ArrayBuffer.empty[Person]

  def initPopulation(lock: Object): Unit = lock.synchronized {
    people.clear()
    board.fields.flatten.foreach(_.clear())
    val healthyCount = TOTAL_PEOPLE - INITIAL_INFECTED
    val random = new Random()
    for (i <- 0 until healthyCount) people += new BasicPerson(random.nextInt(BOARD_WIDTH), random.nextInt(BOARD_HEIGHT), false, board)
    for (_ <- 0 until INITIAL_INFECTED) people += new BasicPerson(random.nextInt(BOARD_WIDTH), random.nextInt(BOARD_HEIGHT), true, board)
  }

  private def movement_turn(): Unit = {
    board.fields.flatten.foreach(field => field.clear())
    people.foreach(person => person.make_step())
  }

  private def infection_turn(): Unit = {
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

  private def infection_progress_turn(): Unit = {
    people.foreach(p => p.tick(disease))
  }

  def turn(): Unit = {
    infection_progress_turn()
    movement_turn()
    infection_turn()
  }
}