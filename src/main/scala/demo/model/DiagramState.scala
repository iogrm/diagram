package demo.model

case class DiagramState(
    userId: UserId,
    currentStatus: DiagramStatus,
    orderId: Option[OrderId],
    orderStatus: Option[DiagramStatus]
)
