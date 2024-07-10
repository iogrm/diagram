package demo.module

import akka.http.scaladsl.server.Route
import demo.service.DiagramService
import demo.service.ComplaintService
import demo.api.controller.DiagramController
import com.redis.RedisClient
import demo.service.OrderService

class DiagramModule(
    complaintService: ComplaintService,
    orderService: OrderService,
    redisRepo: RedisClient
) {
  def build(): Route = {
    val service = new DiagramService(complaintService, orderService, redisRepo)
    val route = new DiagramController(service).route
    route
  }
}
