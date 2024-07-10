package demo.persistence.repository

import demo.complaint.persist.Complaint
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler}
import scala.util.Try
import reactivemongo.api.Cursor
import reactivemongo.api.bson.{Macros, BSONObjectID}
import demo.complaint.persist.bson.ComplaintIdBsonSupport
import demo.persistence.MongoConnector
import demo.model.ComplaintId

class ComplaintRepo(connector: MongoConnector) {

  private val col = connector.complaints
  import ComplaintIdBsonSupport.complaintIdHandler
  private implicit val complaintHandler: BSONDocumentHandler[Complaint] =
    Macros.handler[Complaint]

  def getOne(
      complaintId: ComplaintId
  )(implicit ec: ExecutionContext): Future[Option[Complaint]] = {
    val query = BSONDocument("_id" -> complaintId)
    col.find(query).one[Complaint]
  }

  def getAll()(implicit ec: ExecutionContext): Future[List[Complaint]] = {
    col
      .find(BSONDocument())
      .cursor[Complaint]()
      .collect[List](-1, Cursor.FailOnError[List[Complaint]]())
  }

  def insert(
      complaint: Complaint
  )(implicit ec: ExecutionContext): Future[Unit] =
    col
      .insert(false)
      .one(complaint)
      .map(_ => ())
}
