package demo.complaint.persist.bson

import reactivemongo.api.bson.BSONHandler

import reactivemongo.api.bson.Macros
import demo.model.ComplaintId

object ComplaintIdBsonSupport {
  implicit val complaintIdHandler: BSONHandler[ComplaintId] =
    Macros.handler[ComplaintId]
}
