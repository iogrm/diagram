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
import demo.service.DiagramService
import demo.model.UserId
import demo.api.dto.DiagramDto

class DiagramController(service: DiagramService) {

  private val log = LoggerFactory.getLogger(getClass)

  import GetComplaintJsonSupport.{resultHandler}

  private val diagramRoute: Route =
    optionalHeaderValueByName("X-User-Id") {
      case Some(userId) =>
        pathPrefix("api" / "diagram") {
          path("message") {
            post {
              import demo.json.DiagramJsonSupport.{paramHandler, resultHandler}
              entity(as[DiagramDto.Param]) { message =>
                complete(service.command(UserId(userId), message))
              }
            }
          }
        }
      case None => complete(s"No user was provided")
    }

  val route: Route = diagramRoute
}
