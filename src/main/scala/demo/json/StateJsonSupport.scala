package demo.json

import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject
import spray.json.DefaultJsonProtocol._
import demo.common.JsonSupport
import demo.api.dto.AddComplaint
import demo.model.DiagramState

object StateJsonSupport {
  import UserIdJsonSupport.formatUserId
  import OrderIdJsonSupport.formatOrderId
  import DiagramStatusJsonSupport.diagramStatusFormat
  implicit val format: RootJsonFormat[DiagramState] = jsonFormat4(DiagramState)
}
