package demo.common

import spray.json.JsonFormat
import spray.json.JsValue
import spray.json.JsString
import demo.common.Id
object JsonSupport {

  val tag = "_TAG"
  def idFormat[T <: Id](constructor: String => T) =
    new JsonFormat[T] {
      override def read(json: JsValue): T =
        json match {
          case JsString(value) => constructor(value)
          case _ =>
            throw new Exception("Expected simple string but got " + json)
        }

      override def write(obj: T): JsValue = JsString(obj.value)
    }
}
