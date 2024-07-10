package demo.json

import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject
import spray.json.DefaultJsonProtocol._
import demo.common.JsonSupport
import demo.api.dto.AddComplaint
import demo.api.dto.AllComplaint
import demo.api.dto.GetComplaint

object AddComplaintJsonSupport {

  import ComplaintIdJsonSupport.formatComplaintId

  implicit val paramHandler: RootJsonFormat[AddComplaint.Param] =
    jsonFormat1(AddComplaint.Param)

  implicit val resultHandler: RootJsonFormat[AddComplaint.Result] =
    jsonFormat1(AddComplaint.Result)

}

object AllComplaintJsonSupport {

  import GetComplaintJsonSupport.resultHandler
  implicit def allResultHandler: RootJsonFormat[AllComplaint.Result] =
    jsonFormat1(
      AllComplaint.Result
    )
}

object GetComplaintJsonSupport {

  import ComplaintIdJsonSupport.formatComplaintId

  implicit val resultHandler: RootJsonFormat[GetComplaint.Result] = jsonFormat2(
    GetComplaint.Result
  )
}
