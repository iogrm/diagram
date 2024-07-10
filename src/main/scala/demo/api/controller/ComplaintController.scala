package demo.api.controller

import scala.util.{Success, Failure}
import akka.http.scaladsl.model.StatusCodes
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import demo.model.ComplaintId
import demo.json.{
  GetComplaintJsonSupport,
  AddComplaintJsonSupport,
  AllComplaintJsonSupport
}
import demo.api.dto.AddComplaint
import demo.service.ComplaintService

class ComplaintController(service: ComplaintService) {

  private val log = LoggerFactory.getLogger(getClass)

  val route: Route = pathPrefix("api" / "complaint") {

    import AddComplaintJsonSupport.{resultHandler, paramHandler}
    path("add") {
      post {
        entity(as[AddComplaint.Param]) { body =>
          val complaint = service.addComplaint(body)
          onComplete(complaint) {
            case Success(value) => complete(value)
            case Failure(ex) =>
              ex.printStackTrace()
              log.error(s"Failed to process due to ", ex)
              complete(
                status = StatusCodes.InternalServerError,
                "Failed to execute"
              )
          }
        }
      }
    } ~ path("all") {
      get {

        import GetComplaintJsonSupport.{resultHandler}
        onComplete(service.getAll()) {
          case Success(value) =>
            complete(value)

          case Failure(ex) =>
            ex.printStackTrace()
            log.error(s"Failed to process due to ", ex)
            complete(
              status = StatusCodes.InternalServerError,
              "Failed to execute"
            )
        }
      }
    } ~ path(RemainingPath) {
      case (id) => {
        import GetComplaintJsonSupport.resultHandler
        complete(service.getComplaint(ComplaintId(id.toString())))
      }
    }
  }

}
