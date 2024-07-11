package demo.api.controller

import scala.util.{Success, Failure}
import akka.http.scaladsl.model.StatusCodes
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import demo.model.StateId
import demo.json.{GetStateJsonSupport, AddStateJsonSupport, AllStateJsonSupport}
import demo.api.dto.AddState
import demo.service.StateService
import demo.state.persist.StateEntity
import demo.api.dto.DiagramDto
import demo.model.DiagramId
import demo.model.UserId

class StateController(service: StateService) {

  private val log = LoggerFactory.getLogger(getClass)

  // private val stateRoute: Route = pathPrefix("api" / "state") {

  //   import demo.json.StateJsonSupport.format
  //   path("add") {
  //     post {
  //       entity(as[StateEntity]) { body =>
  //         val state = service.addState(body)
  //         onComplete(state) {
  //           case Success(value) => complete(value)
  //           case Failure(ex) =>
  //             ex.printStackTrace()
  //             log.error(s"Failed to process due to ", ex)
  //             complete(
  //               status = StatusCodes.InternalServerError,
  //               "Failed to execute"
  //             )
  //         }
  //       }
  //     }
  //   } ~ path("all") {
  //     get {

  //       import GetStateJsonSupport.{resultHandler}
  //       onComplete(service.getAll()) {
  //         case Success(value) =>
  //           complete(value)

  //         case Failure(ex) =>
  //           ex.printStackTrace()
  //           log.error(s"Failed to process due to ", ex)
  //           complete(
  //             status = StatusCodes.InternalServerError,
  //             "Failed to execute"
  //           )
  //       }
  //     }
  //   } ~ path(RemainingPath) {
  //     case (id) => {
  //       import GetStateJsonSupport.resultHandler
  //       complete(service.getState(StateId(id.toString())))
  //     }
  //   }
  // }
  import GetStateJsonSupport.{resultHandler}

  private val diagramRoute: Route =
    optionalHeaderValueByName("X-User-Id") {

      case Some(userId) =>
        pathPrefix("api" / "diagram") {
          path("message") {
            post {
              import demo.json.DiagramJsonSupport.{paramHandler, resultHandler}
              entity(as[DiagramDto.Param]) { message =>
                complete(
                  service.command(UserId(userId), DiagramId("1"), message)
                )
              }
            }
          }
        }
      case None => complete(s"No user was provided")
    }

  val route = diagramRoute

}
