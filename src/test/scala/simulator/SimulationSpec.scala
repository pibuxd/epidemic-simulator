package simulator

import com.typesafe.config.ConfigFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimulationSpec extends AnyFlatSpec with Matchers {

  "A Simulation" should "initialize population correctly" in {
    val config = ConfigFactory.load()
    val width = config.getInt("simulator.board.width")
    val height = config.getInt("simulator.board.height")
    val population = config.getInt("simulator.population.total")
    val initialInfected = config.getInt("simulator.population.initial_infected")

    val sim = new Simulation(width, height, population, initialInfected, config)
    val lock = new Object()
    
    sim.initPopulation(lock)

    sim.people should not be empty
    
    val infectedCount = sim.people.count(_.infected)
    infectedCount should be > 0
  }
  
  it should "not crash during a turn execution" in {
    val config = ConfigFactory.load()
    val width = config.getInt("simulator.board.width")
    val height = config.getInt("simulator.board.height")
    val population = config.getInt("simulator.population.total")
    val initialInfected = config.getInt("simulator.population.initial_infected")

    val sim = new Simulation(width, height, population, initialInfected, config)
    val lock = new Object()
    sim.initPopulation(lock)
    
    noException should be thrownBy sim.turn()
  }
}