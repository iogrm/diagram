package demo.service

import scala.concurrent.{ExecutionContext, Future}
import demo.complaint.persist.Complaint
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID
import scala.util.{Success, Failure}
import demo.api.dto.{AddComplaint, GetComplaint}
import demo.model.ComplaintId
import demo.persistence.repository.ComplaintRepo
import demo.model.DiagramStatus

class ComplaintService(repo: ComplaintRepo) {

  def addComplaint(param: AddComplaint.Param)(implicit
      ec: ExecutionContext
  ): Future[AddComplaint.Result] = {

    val complaint =
      Complaint(
        _id = ComplaintId(randomId),
        product = param.complaint
      )

    for {
      _ <- repo.insert(complaint)
    } yield AddComplaint.Result(complaint._id)

  }

  def getComplaint(complaintId: ComplaintId)(implicit
      ec: ExecutionContext
  ): Future[GetComplaint.Result] = {
    repo
      .getOne(complaintId)
      .map(option =>
        option match {
          case Some(complaint) =>
            GetComplaint.Result(
              complaint._id,
              complaint.product
            )
          case None => {
            println("getComplaint Error!!!")
            println(complaintId)
            ???
          }
        }
      )
  }

  def getAll()(implicit
      ec: ExecutionContext
  ): Future[List[GetComplaint.Result]] = {
    repo
      .getAll()
      .map(
        _.map(complaint =>
          GetComplaint.Result(
            complaint._id,
            complaint.product
          )
        )
      )
  }

  def randomId = UUID.randomUUID().toString()
}
