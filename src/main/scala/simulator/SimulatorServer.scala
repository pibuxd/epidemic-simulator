package simulator

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, Sink, Source}
import spray.json._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.collection.mutable.ArrayBuffer
import simulator.people._
import simulator.fields._
import simulator.disease._

final case class AgentOut(x: Int, y: Int, status: Int)
final case class StateMsg(`type`: String, width: Int, height: Int, agents: Seq[AgentOut])

object JsonProtocol extends DefaultJsonProtocol {
  implicit val agentFmt: RootJsonFormat[AgentOut] = jsonFormat3(AgentOut)
  implicit val stateFmt: RootJsonFormat[StateMsg] = jsonFormat4(StateMsg)
}

object SimulatorServer {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(akka.actor.typed.scaladsl.Behaviors.empty, "sim-server")
    implicit val ec: ExecutionContext = system.executionContext

    val config = system.settings.config
    val serverHost = config.getString("simulator.server.host")
    val serverPort = config.getInt("simulator.server.port")

    import JsonProtocol._
    val BOARD_WIDTH = config.getInt("simulator.board.width")
    val BOARD_HEIGHT = config.getInt("simulator.board.height")
    val LAYERS = config.getInt("simulator.board.layers")
    val TOTAL_PEOPLE = config.getInt("simulator.population.total")
    val INITIAL_INFECTED = config.getInt("simulator.population.initial_infected")
    val DISEASE_TYPE = config.getString("simulator.disease.type")
    val TICK_INTERVAL = config.getInt("simulator.tick_interval_ms")
    
    val disease: Disease = Class.forName(s"simulator.disease.$DISEASE_TYPE")
      .getDeclaredConstructor()
      .newInstance()
      .asInstanceOf[Disease]
    val board = new Board(BOARD_WIDTH, BOARD_HEIGHT, LAYERS)
    val people = ArrayBuffer.empty[Person]
    val healthyCount = TOTAL_PEOPLE - INITIAL_INFECTED
    for (i <- 0 until healthyCount) people += new BasicPerson(i % BOARD_WIDTH, i / BOARD_WIDTH, false, board)
    for (_ <- 0 until INITIAL_INFECTED) people += new BasicPerson(BOARD_WIDTH - 1, BOARD_HEIGHT - 1, true, board)
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
    val tickSource = Source.tick(0.millis, TICK_INTERVAL.millis, ()).map { _ =>
      movement_turn()
      infection_turn()
      val agentsOut = people.map(p => {
        val pos = p.get_position()
        AgentOut(pos._1, pos._2, if (p.infected) 1 else 0)
      }).toSeq
      val msg = StateMsg("state", BOARD_WIDTH, BOARD_HEIGHT, agentsOut)
      TextMessage.Strict(msg.toJson.compactPrint)
    }
    val hubSource = tickSource.runWith(BroadcastHub.sink(bufferSize = 256))
    val wsFlow = Flow.fromSinkAndSourceCoupled(Sink.ignore, hubSource)
    val route = path("ws") {
      handleWebSocketMessages(wsFlow)
    } ~ get {
      complete("Simulator WS running")
    }
    val bindingFuture = Http().newServerAt(serverHost, serverPort).bind(route)
    bindingFuture.foreach(_ => println(s"SimulatorServer: WebSocket endpoint available at ws://$serverHost:$serverPort/ws"))(ec)
    println("SimulatorServer started, waiting. Press Ctrl+C to stop.")
    Await.result(system.whenTerminated, Duration.Inf)
  }
}