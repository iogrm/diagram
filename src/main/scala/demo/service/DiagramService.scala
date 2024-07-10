package demo.service

import scala.concurrent.Future
import demo.model.UserId
import com.redis.RedisClient
import demo.model.DiagramState
import spray.json._
import DefaultJsonProtocol._
import demo.json.StateJsonSupport

import StateJsonSupport.format
import scala.concurrent.ExecutionContext.Implicits.global
import scala.annotation.switch
import demo.api.dto.DiagramDto
import demo.model.DiagramStatus
import demo.api.dto.AddComplaint
import demo.model.OrderId
import demo.config.Diagram

class DiagramService(
    complaintService: ComplaintService,
    orderService: OrderService,
    redisRepo: RedisClient
) {

  def command(
      userId: UserId,
      param: DiagramDto.Param
  ): Future[DiagramDto.Result] = {
    val userState = redisRepo.get(userId)

    println(userState)
    userState match {
      case Some(data) => {
        val state = data.parseJson.convertTo[DiagramState]
        state.currentStatus match {

          case DiagramStatus.ReceiveOrderNumber() =>
            goNextState(state.copy(orderId = Some(OrderId(param.message))))
            command(userId, param)

          case DiagramStatus.OrderStatus() =>
            if (state.orderId == None) {
              goToStartState(state)
              return sendMessage(Diagram.Message.OrderStatusNone())
            }

            for {
              status <- orderService
                .getOrderStatus(
                  state.orderId.get
                )
              newState = goNextState(state.copy(orderStatus = status))
              message <- status match {
                case Some(DiagramStatus.SetComplaint()) =>
                  sendMessage(Diagram.Message.OrderStatusComplaint())
                case Some(DiagramStatus.OrderTracking()) =>
                  sendMessage(Diagram.Message.OrderStatusPreparation())
                case None =>
                  goToStartState(state)
                  sendMessage(Diagram.Message.OrderStatusNone())
              }
            } yield message

          case DiagramStatus.SetComplaint() =>
            complaintService.addComplaint(AddComplaint.Param(param.message))
            goNextState(state)
            sendMessage(Diagram.Message.ComplaintSaved())

          case DiagramStatus.OrderTracking() =>
            goNextState(state)
            sendMessage(Diagram.Message.OrderTracking())

          case DiagramStatus.End() =>
            param.message match {
              case "start" =>
                goToStartState(state)
                sendMessage(Diagram.Message.Start())
              case _ =>
                sendMessage(Diagram.Message.End())

            }

        }
      }
      case None => {
        param.message match {
          case "start" => {
            goNextState(
              new DiagramState(userId, DiagramStatus.Start(), None, None)
            )
            sendMessage(Diagram.Message.Start())
          }
          case _ =>
            sendMessage(Diagram.Message.Error())

        }
      }
    }

  }

  def goNextState(state: DiagramState): Option[DiagramState] = {
    getNextStep(state) match {
      case Some(value) =>
        val newState =
          state.copy(currentStatus = value)

        redisRepo.set(
          state.userId,
          newState.toJson
        )
        Some(newState)
      case None => None
    }
  }
  def goToStartState(
      state: DiagramState
  ): Option[DiagramState] = {
    goNextState(
      state.copy(
        currentStatus = DiagramStatus.Start()
      )
    )
  }

  def getNextStep(state: DiagramState): Option[DiagramStatus] = {
    state.currentStatus match {
      case DiagramStatus.OrderStatus() => state.orderStatus
      case _ =>
        Diagram.nextStep.get(state.currentStatus)
    }
  }

  def sendMessage(tag: Diagram.Message): Future[DiagramDto.Result] = {
    val message = Diagram.message.get(tag) match {
      case None =>
        throw new Exception(
          s"we don't have tag: ${tag} in sending Message"
        )
      case Some(value) => value
    }
    Future.successful(DiagramDto.Result(message))
  }

}
