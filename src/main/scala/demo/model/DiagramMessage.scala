package demo.model

sealed trait DiagramMessage
object DiagramMessage {

  final case class Start() extends DiagramMessage
  final case class OrderStatusPreparation() extends DiagramMessage
  final case class OrderStatusComplaint() extends DiagramMessage
  final case class OrderStatusNone() extends DiagramMessage
  final case class ComplaintSaved() extends DiagramMessage
  final case class OrderTracking() extends DiagramMessage
  final case class End() extends DiagramMessage
  final case class Error() extends DiagramMessage

}
