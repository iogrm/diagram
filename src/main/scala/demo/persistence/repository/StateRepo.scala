package demo.persistence.repository

import demo.state.persist.State
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler}
import scala.util.Try
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{Macros, BSONObjectID}
import demo.persistence.MongoConnector
import demo.model.StateId
import demo.model.DiagramId
import demo.state.persist.StateEntity

class StateRepo(connector: MongoConnector) {

  private val col = connector.states
  import demo.state.persist.bson.StateIdBsonSupport.stateIdHandler
  import demo.state.persist.bson.DiagramIdBsonSupport.digramIdHandler

  private implicit val stateHandler: BSONDocumentHandler[StateEntity] =
    Macros.handler[StateEntity]

  def getOne(
      stateId: StateId
  )(implicit ec: ExecutionContext): Future[Option[StateEntity]] = {
    val query = BSONDocument("_id" -> stateId)
    col.find(query).one[StateEntity]
  }

  def getStartState(
      diagramId: DiagramId
  )(implicit ec: ExecutionContext): Future[Option[StateEntity]] = {
    val query = BSONDocument("diagramId" -> diagramId, "start" -> true)
    col.find(query).one[StateEntity]
  }

  def getAll()(implicit ec: ExecutionContext): Future[List[StateEntity]] = {
    col
      .find(BSONDocument())
      .cursor[StateEntity]()
      .collect[List](-1, Cursor.FailOnError[List[StateEntity]]())
  }

  def insert(
      state: StateEntity
  )(implicit ec: ExecutionContext): Future[Unit] = {
    col
      .insert(false)
      .one(state)
      .map(x => ())
  }
}
