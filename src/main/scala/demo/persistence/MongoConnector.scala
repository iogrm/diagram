package demo.persistence
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import reactivemongo.api.DB
import reactivemongo.api.bson.BSONDocument

class MongoConnector {

  private val db: DB = {
    println("Mongo Connecting...")
    val driver = AsyncDriver()
    val connection = driver.connect("mongodb://127.0.0.1:27017")
    Await.result(connection.flatMap(_.database("gapify")), 20.seconds)
  }

  val states = getCol("states")
  val messages = getCol("messages")

  def getCol(name: String): BSONCollection = {
    db.collection(name)
  }

}
