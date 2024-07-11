package demo.state.persist

import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.bson.BSONDocumentReader
import reactivemongo.api.bson.BSONDocument
import demo.model.StateId
import demo.model.DiagramId
import reactivemongo.api.bson.Macros
import reactivemongo.api.bson.BSONHandler
import demo.state.persist.State.Tag

object State {
  sealed trait Tag
  object Tag {
    final case class Condition() extends Tag
    final case class State() extends Tag

    // implicit val condition = Macros.handler[Condition]
    // implicit val state = Macros.handler[State]

    // implicit val handler: BSONHandler[Tag] = Macros.handler[Tag]
  }

}

final case class StateEntity(
    _id: StateId,
    diagramId: DiagramId,
    tag: String,
    name: String,
    response: Option[String],
    next: Option[StateId],
    callback: Option[String],
    start: Boolean,
    end: Boolean
)
