package demo.module

import demo.persistence.RedisConnector
import demo.persistence.MongoConnector
import demo.service.StateService
import com.redis.RedisClient
import akka.http.scaladsl.server.Route
import demo.manager.RequestManager
import demo.persistence.repository.StateRepo
import demo.api.controller.StateController
import scala.concurrent.ExecutionContext
import demo.persistence.repository.MessageRepo

class AppModule {
  def build: (Route, StateService) = {
    val connector = new MongoConnector()
    val redisClient = new RedisConnector().db
    val manager = new RequestManager()
    val stateRepo = new StateRepo(connector)
    val messageRepo = new MessageRepo(connector)
    val stateService =
      new StateService(redisClient, stateRepo, manager, messageRepo)
    val route = new StateController(stateService).route
    (route, stateService)
  }
}
