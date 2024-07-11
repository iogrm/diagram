package demo.json

import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject
import spray.json.DefaultJsonProtocol._
import demo.common.JsonSupport
import demo.api.dto.AddState
import demo.api.dto.DiagramDto
import demo.model.CacheState

object DiagramJsonSupport {

  implicit val paramHandler: RootJsonFormat[DiagramDto.Param] =
    jsonFormat1(DiagramDto.Param)

  implicit val resultHandler: RootJsonFormat[DiagramDto.Result] =
    jsonFormat1(DiagramDto.Result)

}
