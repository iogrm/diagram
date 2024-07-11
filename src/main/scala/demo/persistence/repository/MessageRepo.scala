package demo.persistence.repository

import demo.state.persist.State
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler}
import scala.util.Try
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{Macros, BSONObjectID}
import demo.persistence.MongoConnector
import demo.model.MessageId
import demo.model.DiagramId
import demo.state.persist.MessageEntity
import demo.model.StateId

class MessageRepo(connector: MongoConnector) {

  private val col = connector.messages
  import demo.state.persist.bson.MessageIdBsonSupport.messageIdHandler
  import demo.state.persist.bson.DiagramIdBsonSupport.digramIdHandler
  import demo.state.persist.bson.UserIdBsonSupport.userIdHandler
  import demo.state.persist.bson.StateIdBsonSupport.stateIdHandler

  private implicit val stateHandler: BSONDocumentHandler[MessageEntity] =
    Macros.handler[MessageEntity]

  def getOne(
      messageId: MessageId
  )(implicit ec: ExecutionContext): Future[Option[MessageEntity]] = {
    val query = BSONDocument("_id" -> messageId)
    col.find(query).one[MessageEntity]
  }

  def getAllByStateId(stateId: StateId)(implicit
      ec: ExecutionContext
  ): Future[List[MessageEntity]] = {
    col
      .find(BSONDocument("stateId" -> stateId))
      .cursor[MessageEntity]()
      .collect[List](-1, Cursor.FailOnError[List[MessageEntity]]())
  }

  def getAll()(implicit ec: ExecutionContext): Future[List[MessageEntity]] = {
    col
      .find(BSONDocument())
      .cursor[MessageEntity]()
      .collect[List](-1, Cursor.FailOnError[List[MessageEntity]]())
  }

  def insert(
      message: MessageEntity
  )(implicit ec: ExecutionContext): Future[Unit] = {
    col
      .insert(false)
      .one(message)
      .map(x => ())
  }
}
