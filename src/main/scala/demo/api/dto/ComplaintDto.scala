package demo.api.dto
import demo.model.ComplaintId

object AddComplaint {

  final case class Result(complaintId: ComplaintId)

  final case class Param(
      complaint: String
  )
}

object AllComplaint {
  final case class Result(complaints: List[GetComplaint.Result])
}

object GetComplaint {

  final case class Result(
      id: ComplaintId,
      complaint: String
  )

  final case class Param(complaintId: ComplaintId)
}
