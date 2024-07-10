package demo.module

import akka.http.scaladsl.server.Route
import demo.persistence.MongoConnector
import demo.persistence.repository.ComplaintRepo
import demo.service.ComplaintService;
import demo.api.controller.ComplaintController

class ComplaintModule(connector: MongoConnector) {
  def build(): (Route, ComplaintService) = {
    val repo = new ComplaintRepo(connector)
    val service = new ComplaintService(repo)
    val route = new ComplaintController(service).route
    (route, service)
  }
}
