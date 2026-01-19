package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import simulator.people._
import simulator.disease._
import simulator.fields._

class TestDisease(rec: Int, mort: Double) extends Disease {
  override def get_name(): String = "TestVid"
  override def get_base_infection_probability(): Double = 1.0
  override def get_recovery_time(): Int = rec
  override def get_mortality_rate(): Double = mort
  
  override def get_incubation_period(): Int = 0 
  
  override def is_symptomatic_infectious(): Boolean = true 
  
  override def get_max_infection_distance(): Int = 1
}

class PersonSpec extends AnyFlatSpec with Matchers {

  "A Person" should "recover after recovery time passes" in {
    val board = new Board(5, 5, 1)
    val safeDisease = new TestDisease(rec = 3, mort = 0.0)
    val person = new BasicPerson(0, 0, true, board)

    person.tick(safeDisease)
    person.infected should be (true)
    
    person.tick(safeDisease)
    person.infected should be (true)

    person.tick(safeDisease)
    person.infected should be (false)
    person.dead should be (false)
  }

  it should "die if mortality is 100%" in {
    val board = new Board(5, 5, 1)
    val deadlyDisease = new TestDisease(rec = 10, mort = 1.0)
    val person = new BasicPerson(0, 0, true, board)

    person.tick(deadlyDisease)
    
    person.dead should be (true)
    person.infected should be (false)
  }
}