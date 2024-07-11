package demo.manager

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import scala.concurrent.Future
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import demo.api.dto.GetRequest

class RequestManager() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val meteralizer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  def request(
      url: String,
      message: String
  ): Future[GetRequest.Result] = {

    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = url + message
    )
    import system.dispatcher

    import demo.json.GetRequestJsonSupport.resultHandler
    Http()
      .singleRequest(request)
      .flatMap(_.entity.toStrict(2.seconds))
      .flatMap(entity => Unmarshal(entity).to[GetRequest.Result])
  }
}
