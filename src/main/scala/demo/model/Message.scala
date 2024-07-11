package demo.model

object Message {

  final case class Count(stateId: StateId, count: Long)
  final case class Analyze(stateId: StateId, percent: Double)
}
