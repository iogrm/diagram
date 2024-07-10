package demo.complaint.persist

import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.bson.BSONDocumentReader
import reactivemongo.api.bson.BSONDocument
import demo.model.ComplaintId

final case class Complaint(
    _id: ComplaintId,
    product: String
)
