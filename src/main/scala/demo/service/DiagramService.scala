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
import demo.model.DiagramMessage
import demo.api.dto.AddComplaint
import demo.model.OrderId

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

    print(userState)
    userState match {
      case Some(data) => {
        val state = data.parseJson.convertTo[DiagramState]
        state.currentStatus match {
          case DiagramStatus.ReceiveOrderNumber() =>
            goNextState(state.copy(orderId = Some(OrderId(param.message))))
            command(userId, param)
          case DiagramStatus.OrderStatus() =>
            for {
              status <- orderService
                .getOrderStatus(
                  state.orderId.get
                )
              newState = goNextState(state.copy(orderStatus = status))
              message <- status match {
                case Some(DiagramStatus.SetComplaint()) =>
                  sendMessage(DiagramMessage.OrderStatusComplaint())
                case Some(DiagramStatus.OrderTracking()) =>
                  sendMessage(DiagramMessage.OrderStatusPreparation())
                case None =>
                  sendMessage(DiagramMessage.OrderStatusNone())
              }
            } yield message

          case DiagramStatus.SetComplaint() =>
            complaintService.addComplaint(AddComplaint.Param(param.message))
            goNextState(state)
            sendMessage(DiagramMessage.ComplaintSaved())

          case DiagramStatus.OrderTracking() =>
            goNextState(state)
            sendMessage(DiagramMessage.OrderTracking())

          case DiagramStatus.End() =>
            param.message match {
              case "start" => command(userId, param)
              case _ =>
                sendMessage(DiagramMessage.End())

            }

        }
      }
      case None => {
        param.message match {
          case "start" => {
            goNextState(
              new DiagramState(userId, DiagramStatus.Start(), None, None)
            )
            sendMessage(DiagramMessage.Start())
          }
          case _ =>
            sendMessage(DiagramMessage.Error())

        }
      }
    }

  }

  def start(userId: UserId): DiagramState = {
    val state = new DiagramState(userId, DiagramStatus.Start(), None, None)
    redisRepo.set(state.userId, state.toJson)
    state
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

  def getNextStep(state: DiagramState): Option[DiagramStatus] = {
    state.currentStatus match {
      case DiagramStatus.OrderStatus() => state.orderStatus
      case _ =>
        nextStep.get(state.currentStatus)
    }
  }

  def sendMessage(tag: DiagramMessage): Future[DiagramDto.Result] = {
    val message = messages.get(tag) match {
      case None        => s"we don't have tag: ${tag} in sending Message"
      case Some(value) => value
    }
    Future.successful(DiagramDto.Result(message))
  }

  val messages: Map[DiagramMessage, String] =
    Map.apply(
      DiagramMessage.Start() -> "شماره سفارش خود را وارد نمایید",
      DiagramMessage.OrderTracking() -> "سفارش شما در حال پیگیری است",
      DiagramMessage
        .OrderStatusComplaint() -> "در صورتی از خدمات ارائه شده راضی نبودید، شکایت خود را مطرح نمایید",
      DiagramMessage
        .OrderStatusPreparation() -> "سفارش شما در حال پیگیری است",
      DiagramMessage
        .OrderStatusNone() -> "چنین شماره سفارشی در سیستم موجود نمیباشد",
      DiagramMessage.ComplaintSaved() -> "شکایت شما در اسرع وقت بررسی میشود",
      DiagramMessage.End() -> "اگر مایل به شروع دوباره هستید شروع را بزنید.",
      DiagramMessage.Error() -> "اگر مایه به شروع هستید بنویسید start."
    )

  val nextStep: Map[DiagramStatus, DiagramStatus] =
    Map.apply(
      DiagramStatus.Start() -> DiagramStatus.ReceiveOrderNumber(),
      DiagramStatus.ReceiveOrderNumber() -> DiagramStatus.OrderStatus(),
      DiagramStatus.SetComplaint() -> DiagramStatus.End(),
      DiagramStatus.OrderTracking() -> DiagramStatus.End()
    )

}
