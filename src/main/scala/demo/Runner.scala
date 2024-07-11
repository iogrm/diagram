package gapify

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import demo.module.AppModule

import scala.concurrent.ExecutionContext.Implicits.global

object Runner {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("gapify-http-system")

    val (route, service) = new AppModule().build
    service.init

    val bindingFuture =
      Http().newServerAt("0.0.0.0", 8080).bind(route)
    println("Server Running")
  }
}
