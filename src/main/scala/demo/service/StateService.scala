package demo.service

import scala.concurrent.{ExecutionContext, Future}
import demo.state.persist.State
import java.util.UUID
import scala.util.{Success, Failure, Try}
import demo.api.dto.{AddState, GetState}
import demo.model.StateId
import demo.persistence.repository.StateRepo
import demo.model.UserId
import demo.api.dto.DiagramDto
import com.redis.RedisClient
import demo.model.DiagramId

import spray.json._
import DefaultJsonProtocol._
import demo.state.persist.StateEntity
import demo.json.CacheStateJsonSupport.format
import demo.manager.RequestManager
import demo.model.CacheState

class StateService(
    redisRepo: RedisClient,
    repo: StateRepo,
    manager: RequestManager
) {

  def command(userId: UserId, diagramId: DiagramId, param: DiagramDto.Param)(
      implicit ec: ExecutionContext
  ): Future[String] = {

    val cacheData = redisRepo.get(userId)
    println("-------------------------------------------")
    println("cacheData: ")
    println(cacheData)
    cacheData match {
      case Some(data) => {

        import demo.json.CacheStateJsonSupport.format
        val cacheState = data.parseJson.convertTo[CacheState]
        println("cacheState: ")
        println(cacheState)
        val stateFuture = getState(cacheState.stateId)
        stateFuture.flatMap(stateOption => {
          println("stateOption: ")
          println(stateOption)
          stateOption match {
            case Some(state) =>
              println("state: ")
              println(state)
              if (state.end)
                Future(state.response.get)
              else
                state.tag match {
                  case "State" => {
                    recordState(state)
                    setState(CacheState(userId, state.next.get, diagramId))
                    Future(state.response.get)
                  }
                  case "Condition" => {
                    val result =
                      manager.request(state.callback.get, param.message)
                    for {
                      something <- result
                      id = something.id
                      str <- {
                        setState(CacheState(userId, id, diagramId))
                        command(userId, diagramId, param)
                      }
                    } yield str
                  }
                }
          }
        })
      }
      case None => {
        val startState = repo.getStartState(diagramId)
        startState.andThen(x => {
          println("startState: ")
          println(x)
        })

        startState.flatMap(stateOption => {
          val state = stateOption.get
          setState(CacheState(userId, state.next.get, diagramId))
          val message = state.response.get
          Future(message)
        })

      }
    }

  }

  def addState(state: StateEntity)(implicit
      ec: ExecutionContext
  ): Future[AddState.Result] = {

    for {
      _ <- repo.insert(state)
    } yield AddState.Result(state._id)

  }

  def recordState(state: StateEntity) {}
  def setState(state: CacheState) {
    redisRepo.set(
      state.userId,
      state.toJson
    )
  }

  def getState(stateId: StateId)(implicit
      ec: ExecutionContext
  ): Future[Option[StateEntity]] = {
    repo.getOne(stateId)

  }

  def getAll()(implicit
      ec: ExecutionContext
  ): Future[List[StateEntity]] = {
    repo.getAll()
  }

  def init(implicit
      ec: ExecutionContext
  ): Unit = {
    val diagramId = DiagramId("1")

    val message1 = "شماره سفارش خود را وارد نمایید"
    val message2 = "سفارش شما در حال پیگیری است"
    val message3 =
      "در صورتی از خدمات ارائه شده راضی نبودید، شکایت خود را مطرح نمایید"
    val message4 =
      "چنین شماره سفارشی در سیستم موجود نمیباشد، لطفا مجدد وارد نمایید"
    val message5 = "شکایت شما در اسرع وقت بررسی میشود"

    val complaintSaved = StateEntity(
      StateId(randomId),
      diagramId,
      "State",
      "complaintSaved",
      Some(message5),
      None,
      None,
      false,
      true
    )
    val orderTracking = StateEntity(
      StateId(randomId),
      diagramId,
      "State",
      "orderTracking",
      Some(message2),
      None,
      None,
      false,
      true
    )
    val getComplaint = StateEntity(
      StateId(randomId),
      diagramId,
      "State",
      "getComplaint",
      Some(message3),
      Some(complaintSaved._id),
      None,
      false,
      false
    )
    val orderStatus = StateEntity(
      StateId(randomId),
      diagramId,
      "Condition",
      "orderStatus",
      Some(message4),
      None,
      Some("http://localhost:8000/api/test/"),
      false,
      false
    )
    val receive = StateEntity(
      StateId(randomId),
      diagramId,
      "State",
      "receive",
      Some(message1),
      Some(orderStatus._id),
      None,
      true,
      false
    )
    addState(complaintSaved)
    addState(orderTracking)
    addState(getComplaint)
    addState(orderStatus)
    addState(receive)
    println("data init")

  }

  def randomId = UUID.randomUUID().toString()
}
