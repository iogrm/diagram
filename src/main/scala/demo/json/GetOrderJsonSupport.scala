package demo.json

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import demo.api.dto.GetOrder

object GetOrderJsonSupport {

  import OrderIdJsonSupport.formatOrderId

  implicit val resultHandler: RootJsonFormat[GetOrder.Result] = jsonFormat3(
    GetOrder.Result
  )
}
