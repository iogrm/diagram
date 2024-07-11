package demo.state.persist.bson

import reactivemongo.api.bson.BSONHandler

import reactivemongo.api.bson.Macros
import demo.model.StateId
import demo.model.DiagramId
import demo.state.persist.State.Tag
import demo.model.MessageId
import demo.model.UserId

object StateIdBsonSupport {
  implicit val stateIdHandler: BSONHandler[StateId] =
    Macros.handler[StateId]
}

object DiagramIdBsonSupport {
  implicit val digramIdHandler: BSONHandler[DiagramId] =
    Macros.handler[DiagramId]
}
object MessageIdBsonSupport {
  implicit val messageIdHandler: BSONHandler[MessageId] =
    Macros.handler[MessageId]
}
object UserIdBsonSupport {
  implicit val userIdHandler: BSONHandler[UserId] =
    Macros.handler[UserId]
}
