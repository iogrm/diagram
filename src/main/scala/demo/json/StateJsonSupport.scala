package demo.json

import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsString
import spray.json.JsObject
import spray.json.DefaultJsonProtocol._
import demo.common.JsonSupport
import demo.api.dto.AddState
import demo.state.persist.State
import demo.state.persist.StateEntity
import demo.api.dto.AllState
import demo.api.dto.GetState
import demo.model.CacheState

object CacheStateJsonSupport {

  import StateIdJsonSupport.formatStateId
  import UserIdJsonSupport.formatUserId
  import DiagramIdJsonSupport.formatDiagramId
  implicit val format: RootJsonFormat[CacheState] =
    jsonFormat3(CacheState)

}

object StateJsonSupport {
  import StateIdJsonSupport.formatStateId
  import DiagramIdJsonSupport.formatDiagramId
  import StateTagJsonSupport.formatStateTag
  implicit val format: RootJsonFormat[StateEntity] = jsonFormat9(StateEntity)
}

object StateTagJsonSupport {

  val tag = "_TAG"

  private object Formats {
    val condition = jsonFormat0(
      State.Tag.Condition
    )
    val state = jsonFormat0(
      State.Tag.State
    )
  }

  private object Name {
    val condition = "condition"
    val state = "state"
  }

  implicit val formatStateTag: RootJsonFormat[State.Tag] =
    new RootJsonFormat[State.Tag] {
      override def read(json: JsValue): State.Tag = {
        json.asJsObject.fields(tag) match {
          case JsString(value) =>
            value match {
              case Name.state =>
                Formats.state.read(json)
              case Name.condition =>
                Formats.condition.read(json)

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

      override def write(obj: State.Tag): JsValue = {
        val (name, js) = obj match {
          case o: State.Tag.State =>
            Name.state -> Formats.state.write(o)
          case o: State.Tag.Condition =>
            Name.condition -> Formats.condition.write(o)
        }

        val fields =
          js.asJsObject.fields + (tag -> JsString(
            name
          ))
        new JsObject(fields)
      }

    }

}

object AddStateJsonSupport {

  import StateIdJsonSupport.formatStateId

  implicit val paramHandler: RootJsonFormat[AddState.Param] =
    jsonFormat1(AddState.Param)

  implicit val resultHandler: RootJsonFormat[AddState.Result] =
    jsonFormat1(AddState.Result)

}

object AllStateJsonSupport {

  import GetStateJsonSupport.resultHandler
  implicit def allResultHandler: RootJsonFormat[AllState.Result] =
    jsonFormat1(
      AllState.Result
    )
}

object GetStateJsonSupport {

  import StateIdJsonSupport.formatStateId

  implicit val resultHandler: RootJsonFormat[GetState.Result] = jsonFormat2(
    GetState.Result
  )
}
