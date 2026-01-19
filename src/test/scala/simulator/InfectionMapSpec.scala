package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import simulator.fields._
import simulator.people._
import simulator.disease._

class InfectiousDisease extends Disease {
  override def get_name(): String = "TestVid"
  override def get_base_infection_probability(): Double = 1.0
  override def get_recovery_time(): Int = 10
  override def get_mortality_rate(): Double = 0.0
  override def get_incubation_period(): Int = 0
  override def is_symptomatic_infectious(): Boolean = true
  override def get_max_infection_distance(): Int = 1
}

class InfectionMapSpec extends AnyFlatSpec with Matchers {

  "InfectionMap" should "calculate danger zone around infected person" in {
    val board = new Board(3, 3, 1)
    val disease = new InfectiousDisease()
    
    val patientZero = new BasicPerson(1, 1, true, board)
    
    val map = new InfectionMap(board, disease)
    map.calculate()
    
    val dangerLevel = map.getProbability(1, 0)
    dangerLevel should be > 0.0
  }

  it should "have 0 probability on empty board" in {
    val board = new Board(3, 3, 1)
    val disease = new InfectiousDisease()
    val map = new InfectionMap(board, disease)
    map.calculate()
    
    map.getProbability(0, 0) should be (0.0)
  }
}