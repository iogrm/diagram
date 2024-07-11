package demo.json

import spray.json.RootJsonFormat
import demo.api.dto.GetMessage
import demo.api.dto.AllMessage
import spray.json.DefaultJsonProtocol._
import demo.state.persist.MessageEntity

object AllMessageJsonSupport {

  import MessageJsonSupport.resultHandler
  implicit def handler: RootJsonFormat[AllMessage.Result] =
    jsonFormat1(
      AllMessage.Result
    )
}

object GetMessageJsonSupport {

  import MessageIdJsonSupport.formatMessageId
  import UserIdJsonSupport.formatUserId
  import DiagramIdJsonSupport.formatDiagramId

  implicit val resultHandler: RootJsonFormat[GetMessage.Result] = jsonFormat3(
    GetMessage.Result
  )
}

object MessageJsonSupport {

  import MessageIdJsonSupport.formatMessageId
  import UserIdJsonSupport.formatUserId
  import DiagramIdJsonSupport.formatDiagramId
  import StateIdJsonSupport.formatStateId

  implicit val resultHandler: RootJsonFormat[MessageEntity] = jsonFormat5(
    MessageEntity
  )
}
