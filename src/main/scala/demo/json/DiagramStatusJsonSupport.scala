package demo.json

import spray.json.DefaultJsonProtocol._
import demo.model.DiagramStatus
import spray.json.JsonFormat
import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject

object DiagramStatusJsonSupport {

  val tag = "_TAG"

  private object Formats {
    val start = jsonFormat0(
      DiagramStatus.Start
    )
    val setComplaint = jsonFormat0(
      DiagramStatus.SetComplaint
    )
    val receiveOrderNumber = jsonFormat0(
      DiagramStatus.ReceiveOrderNumber
    )
    val orderTracking = jsonFormat0(
      DiagramStatus.OrderTracking
    )
    val orderStatus = jsonFormat0(
      DiagramStatus.OrderStatus
    )
    val end = jsonFormat0(
      DiagramStatus.End
    )
  }

  private object Name {
    val start = "start"
    val setComplaint = "setComplaint"
    val receiveOrderNumber = "receiveOrderNumber"
    val orderTracking = "orderTracking"
    val orderStatus = "orderStatus"
    val end = "end"
  }

  implicit val diagramStatusFormat: RootJsonFormat[DiagramStatus] =
    new RootJsonFormat[DiagramStatus] {
      override def read(json: JsValue): DiagramStatus = {
        json.asJsObject.fields(tag) match {
          case JsString(value) =>
            value match {
              case Name.start =>
                Formats.start.read(json)
              case Name.setComplaint =>
                Formats.setComplaint.read(json)
              case Name.receiveOrderNumber =>
                Formats.receiveOrderNumber.read(json)
              case Name.orderTracking =>
                Formats.orderTracking.read(json)
              case Name.orderStatus =>
                Formats.orderStatus.read(json)
              case Name.end =>
                Formats.end.read(json)

              case _ =>
                println("we could not pase value!!!")
                println(value)
                ???
            }
          case x =>
            println("it was not js string!!!")
            println(x)
            ???
        }
      }

      override def write(obj: DiagramStatus): JsValue = {
        val (name, js) = obj match {
          case o: DiagramStatus.Start =>
            Name.start -> Formats.start.write(o)
          case o: DiagramStatus.SetComplaint =>
            Name.setComplaint -> Formats.setComplaint.write(o)
          case o: DiagramStatus.ReceiveOrderNumber =>
            Name.receiveOrderNumber -> Formats.receiveOrderNumber.write(
              o
            )
          case o: DiagramStatus.OrderTracking =>
            Name.orderTracking -> Formats.orderTracking.write(o)
          case o: DiagramStatus.OrderStatus =>
            Name.orderStatus -> Formats.orderStatus.write(o)
          case o: DiagramStatus.End =>
            Name.end -> Formats.end.write(o)
        }

        val fields =
          js.asJsObject.fields + (tag -> JsString(
            name
          ))
        new JsObject(fields)
      }

    }
}
