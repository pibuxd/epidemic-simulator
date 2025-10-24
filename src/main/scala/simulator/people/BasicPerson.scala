package simulator.people

abstract class BasicPerson(x: Int, y: Int) extends Person {

  override protected val position: (Int, Int) = (x, y)
}
