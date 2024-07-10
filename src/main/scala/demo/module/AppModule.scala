package demo.module

import demo.persistence.RedisConnector
import demo.module.ComplaintModule
import demo.module.OrderModule
import demo.module.DiagramModule
import demo.persistence.MongoConnector
import demo.service.ComplaintService
import com.redis.RedisClient
import akka.http.scaladsl.server.Route

class AppModule {
  def build(): Route = {

    val connector = new MongoConnector()
    val redisClient = new RedisConnector().db
    val (complaintRoute: Route, complaintService: ComplaintService) =
      new ComplaintModule(connector).build()
    val orderService = new OrderModule().build()
    val diagramRoute =
      new DiagramModule(complaintService, orderService, redisClient).build()
    val route: Route = diagramRoute
    route
  }
}
