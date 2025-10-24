package simulator

import simulator.people.*

object Main{
  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()
    val board: Board = Board(200,200,3)
    val after = System.currentTimeMillis()
    val time = after - start
    println(s"Board generation costed us $time miliseconds")
    print(board.fields{1}{0}.neighbours{1}.size)
    val people: Seq[Person] = Seq.empty
  }
}