package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SimulationSpec extends AnyFlatSpec with Matchers {

  "A Simulation" should "initialize population correctly" in {
    val sim = new Simulation()
    val lock = new Object()
    
    sim.initPopulation(lock)

    sim.people should not be empty
    
    val infectedCount = sim.people.count(_.infected)
    infectedCount should be > 0
  }
  
  it should "not crash during a turn execution" in {
    val sim = new Simulation()
    val lock = new Object()
    sim.initPopulation(lock)
    
    noException should be thrownBy sim.turn()
  }
}