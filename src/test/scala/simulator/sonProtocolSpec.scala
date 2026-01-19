package simulator

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import spray.json._

class JsonProtocolSpec extends AnyFlatSpec with Matchers {
  import JsonProtocol._

  "JsonProtocol" should "serialize AgentOut correctly" in {
    val agent = AgentOut(x = 5, y = 10, status = 1)
    val json = agent.toJson.compactPrint
    
    json should include ("\"x\":5")
    json should include ("\"y\":10")
    json should include ("\"status\":1")
  }

  it should "serialize StateMsg correctly" in {
    val msg = StateMsg("state", 100, 100, Seq(AgentOut(1, 1, 0)))
    val json = msg.toJson.compactPrint
    
    json should include ("\"type\":\"state\"")
    json should include ("\"agents\":[{")
  }
  
  it should "deserialize Command correctly" in {
    val json = """{"command": "start"}"""
    val cmd = json.parseJson.convertTo[Command]
    
    cmd.command should be ("start")
  }
}