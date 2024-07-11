package demo.state.persist.bson

import reactivemongo.api.bson.BSONHandler

import reactivemongo.api.bson.Macros
import demo.model.StateId
import demo.model.DiagramId
import demo.state.persist.State.Tag

object StateIdBsonSupport {
  implicit val stateIdHandler: BSONHandler[StateId] =
    Macros.handler[StateId]
}

object DiagramIdBsonSupport {
  implicit val digramIdHandler: BSONHandler[DiagramId] =
    Macros.handler[DiagramId]
}
// object DiagramTagBsonSupport {
//   implicit val diagramTagHandler: BSONHandler[Tag] =
//     Macros.handler[Tag]
// }
// object DiagramTagBsonSupport {
//   implicit val condition = Macros.handler[Condition]
//   implicit val state = Macros.handler[Tag.State]

//   implicit val bson: BSONHandler[Tag] = Macros.handler[Tag]
// }
