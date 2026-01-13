package simulator

object Main{
  def main(args: Array[String]): Unit = {
    val simulation = new Simulation()
    val lock = new Object()
    simulation.initPopulation(lock)
    val infected_count = simulation.people.count(_.infected)

    println(s"Starting simulation with ${simulation.people.size} people, $infected_count infected")
    println(s"Disease: ${simulation.disease.get_name()}, base probability: ${simulation.disease.get_base_infection_probability()}")
    println(s"Max infection distance: ${simulation.disease.get_max_infection_distance()} layers\n")
    
    for (i <- 1 to simulation.TURNS) {
      println(s"Turn $i:")
      simulation.turn()
      
      val infected_count = simulation.people.count(_.infected)
      val dead_count = simulation.people.count(_.dead)
      println(s"  Infected people: $infected_count")
      println(s"  Dead people: $dead_count")
    }
    
    println("\n=== Simulation Complete ===")
    val final_infected = simulation.people.count(_.infected)
    val final_dead = simulation.people.count(_.dead)
    println(s"Final infected: $final_infected")
    println(s"Final dead: $final_dead")
  }
}
