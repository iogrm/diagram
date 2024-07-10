package demo.model

sealed trait DiagramStatus
object DiagramStatus {

  final case class Start() extends DiagramStatus
  final case class ReceiveOrderNumber() extends DiagramStatus
  final case class OrderStatus() extends DiagramStatus
  final case class SetComplaint() extends DiagramStatus
  final case class OrderTracking() extends DiagramStatus
  final case class End() extends DiagramStatus

}
