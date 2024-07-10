package demo.api.dto
import demo.model.OrderId

object AddOrder {

  final case class Result(orderId: OrderId)

  final case class Param(
      product: String
  )

  sealed trait Error
  object Error {
    final case class StringValidation(msg: String) extends Error
  }
}

object AllOrder {
  final case class Result(orders: List[GetOrder.Result])
}

object GetOrder {

  final case class Result(
      id: OrderId,
      product: String,
      orderStatus: String
  )

  final case class Param(orderId: OrderId)

  sealed trait Error
  object Error {
    final case class StringValidation(msg: String) extends Error
  }
}
