package demo.api.dto

import demo.model.StateId

object GetRequest {

  final case class Result(
      id: StateId
  )

}
