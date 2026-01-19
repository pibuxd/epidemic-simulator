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
import scala.util.Random
import simulator.people._
import simulator.fields._
import simulator.disease._

final case class AgentOut(x: Int, y: Int, status: Int)
final case class StateMsg(`type`: String, width: Int, height: Int, agents: Seq[AgentOut])
final case class Command(
  command: String,
  width: Option[Int] = None,
  height: Option[Int] = None,
  population: Option[Int] = None,
  initialInfected: Option[Int] = None
)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val agentFmt: RootJsonFormat[AgentOut] = jsonFormat3(AgentOut)
  implicit val stateFmt: RootJsonFormat[StateMsg] = jsonFormat4(StateMsg)
  implicit val commandFmt: RootJsonFormat[Command] = jsonFormat5(Command)
}

object SimulatorServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Any] = ActorSystem(akka.actor.typed.scaladsl.Behaviors.empty, "sim-server")
    implicit val ec: ExecutionContext = system.executionContext

    val config = system.settings.config
    val serverHost = config.getString("simulator.server.host")
    val serverPort = config.getInt("simulator.server.port")
    val TICK_INTERVAL = config.getInt("simulator.tick_interval_ms")


    import JsonProtocol._
    var currentWidth = config.getInt("simulator.board.width")
    var currentHeight = config.getInt("simulator.board.height")
    var currentPop = config.getInt("simulator.population.total")
    var currentInfected = config.getInt("simulator.population.initial_infected")
    var simulation = new Simulation(currentWidth, currentHeight, currentPop, currentInfected, config)

    val lock = new Object()
    var isRunning = false
    simulation.initPopulation(lock)

    val tickSource = Source.tick(0.millis, TICK_INTERVAL.millis, ()).map { _ =>
      lock.synchronized {
        if (isRunning) {
          simulation.turn()
        }
        val agentsOut = simulation.people.map(p => {
          val pos = p.get_position()
          val status = if (p.dead) 2 else if (p.infected) 1 else 0
          AgentOut(pos._1, pos._2, status)
        }).toSeq
        val msg = StateMsg("state", simulation.BOARD_WIDTH, simulation.BOARD_HEIGHT, agentsOut)
        TextMessage.Strict(msg.toJson.compactPrint)
      }
    }

    def restartSimulation(): Unit = {
      isRunning = false
      simulation = new Simulation(currentWidth, currentHeight, currentPop, currentInfected, config)
      simulation.initPopulation(lock)
    }

    val incomingSink = Sink.foreach[akka.http.scaladsl.model.ws.Message] {
      case TextMessage.Strict(text) =>
        try {
          val cmd = text.parseJson.convertTo[Command]
          cmd.command match {
            case "configure" => lock.synchronized {
                cmd.width.foreach(currentWidth = _)
                cmd.height.foreach(currentHeight = _)
                cmd.population.foreach(currentPop = _)
                cmd.initialInfected.foreach(currentInfected = _)
                restartSimulation()
              }
            case "start" => isRunning = true
            case "stop" => isRunning = false
            case "reset" => lock.synchronized { restartSimulation() }
            case _ =>
          }
        } catch { case _: Exception => }
      case _ =>
    }

    val hubSource = tickSource.runWith(BroadcastHub.sink(bufferSize = 256))
    val wsFlow = Flow.fromSinkAndSourceCoupled(incomingSink, hubSource)
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