package demo.config

import demo.model.DiagramStatus

object Diagram {

  val nextStep: Map[DiagramStatus, DiagramStatus] =
    Map.apply(
      DiagramStatus.Start() -> DiagramStatus.ReceiveOrderNumber(),
      DiagramStatus.ReceiveOrderNumber() -> DiagramStatus.OrderStatus(),
      DiagramStatus.SetComplaint() -> DiagramStatus.End(),
      DiagramStatus.OrderTracking() -> DiagramStatus.End()
    )

  sealed trait Message
  object Message {

    final case class Start() extends Message
    final case class OrderStatusPreparation() extends Message
    final case class OrderStatusComplaint() extends Message
    final case class OrderStatusNone() extends Message
    final case class ComplaintSaved() extends Message
    final case class OrderTracking() extends Message
    final case class End() extends Message
    final case class Error() extends Message

  }

  val message: Map[Message, String] =
    Map.apply(
      Message.Start() -> "شماره سفارش خود را وارد نمایید",
      Message.OrderTracking() -> "سفارش شما در حال پیگیری است",
      Message
        .OrderStatusComplaint() -> "در صورتی از خدمات ارائه شده راضی نبودید، شکایت خود را مطرح نمایید",
      Message
        .OrderStatusPreparation() -> "سفارش شما در حال پیگیری است",
      Message
        .OrderStatusNone() -> "چنین شماره سفارشی در سیستم موجود نمیباشد، لطفا مجدد وارد نمایید",
      Message.ComplaintSaved() -> "شکایت شما در اسرع وقت بررسی میشود",
      Message.End() -> "اگر مایل به شروع دوباره هستید شروع را بزنید.",
      Message.Error() -> "اگر مایه به شروع هستید بنویسید start."
    )

}
