package demo.json

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import demo.api.dto.GetRequest
import demo.state.persist.State

object GetRequestJsonSupport {

  import StateIdJsonSupport.formatStateId

  implicit val resultHandler: RootJsonFormat[GetRequest.Result] = jsonFormat1(
    GetRequest.Result
  )
}
