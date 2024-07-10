package demo.json

import spray.json.JsonFormat
import demo.model.ComplaintId
import demo.common.JsonSupport.idFormat
import demo.model.UserId
import demo.model.OrderId

object ComplaintIdJsonSupport {
  implicit val formatComplaintId: JsonFormat[ComplaintId] =
    idFormat[ComplaintId](ComplaintId.apply)
}

object UserIdJsonSupport {
  implicit val formatUserId: JsonFormat[UserId] =
    idFormat[UserId](UserId.apply)
}

object OrderIdJsonSupport {
  implicit val formatOrderId: JsonFormat[OrderId] =
    idFormat[OrderId](OrderId.apply)
}
