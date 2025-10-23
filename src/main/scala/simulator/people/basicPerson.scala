package simulator.people

abstract class basicPerson(x: Int, y: Int) extends Person {

  override protected val position: (Int, Int) = (x, y)
}
