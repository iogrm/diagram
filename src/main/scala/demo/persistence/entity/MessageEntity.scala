package demo.state.persist

import demo.model.MessageId
import demo.model.DiagramId
import demo.model.StateId
import demo.model.UserId

final case class MessageEntity(
    _id: MessageId,
    userId: UserId,
    diagramId: DiagramId,
    stateId: StateId,
    message: String
)
