package demo.json

import spray.json.JsonFormat
import demo.model.StateId
import demo.common.JsonSupport.idFormat
import demo.model.UserId
import demo.model.DiagramId
import demo.state.persist.State
import demo.model.MessageId

object StateIdJsonSupport {
  implicit val formatStateId: JsonFormat[StateId] =
    idFormat[StateId](StateId.apply)
}

object UserIdJsonSupport {
  implicit val formatUserId: JsonFormat[UserId] =
    idFormat[UserId](UserId.apply)
}

object DiagramIdJsonSupport {
  implicit val formatDiagramId: JsonFormat[DiagramId] =
    idFormat[DiagramId](DiagramId.apply)
}

object MessageIdJsonSupport {
  implicit val formatMessageId: JsonFormat[MessageId] =
    idFormat[MessageId](MessageId.apply)
}
