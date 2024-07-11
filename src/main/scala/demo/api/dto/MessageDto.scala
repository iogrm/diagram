package demo.api.dto
import demo.model.MessageId
import demo.model.UserId
import demo.model.DiagramId
import demo.state.persist.MessageEntity

object AllMessage {
  final case class Result(messages: List[MessageEntity])
}

object GetMessage {

  final case class Result(
      id: MessageId,
      message: String,
      userId: UserId
  )

}
