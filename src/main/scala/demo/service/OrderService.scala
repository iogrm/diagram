package demo.service

import scala.concurrent.{ExecutionContext, Future}
import demo.complaint.persist.Complaint
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID
import demo.api.dto.{AddComplaint, GetComplaint}
import demo.model.ComplaintId
import demo.persistence.repository.ComplaintRepo
import demo.model.DiagramStatus
import demo.manager.OrderManager
import demo.model.OrderId
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class OrderService(orderManager: OrderManager) {

  def getOrderStatus(
      orderId: OrderId
  ): Future[Option[DiagramStatus]] = {
    val order = orderManager.getOrder(orderId)
    val status: Future[Option[DiagramStatus]] = order.transform {
      case Success(result) =>
        result.orderStatus match {
          case "Preparation" => Success(Some(DiagramStatus.OrderTracking()))
          case "Finished"    => Success(Some(DiagramStatus.SetComplaint()))
          case _             => Success(None)
        }
      case Failure(ex) => Success(None)

    }
    return status
  }

  def randomId = UUID.randomUUID().toString()
}
