package demo.api.dto
import demo.model.StateId

object AddState {

  final case class Result(stateId: StateId)

  final case class Param(
      state: String
  )
}

object AllState {
  final case class Result(states: List[GetState.Result])
}

object GetState {

  final case class Result(
      id: StateId,
      state: String
  )

  final case class Param(stateId: StateId)
}
