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
import demo.persistence.repository.MessageRepo
import demo.state.persist.MessageEntity
import demo.model.MessageId
import demo.api.dto.AllMessage
import demo.api.dto.GetMessage

class StateService(
    redisRepo: RedisClient,
    stateRepo: StateRepo,
    manager: RequestManager,
    messageRepo: MessageRepo
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
                    recordState(
                      MessageEntity(
                        MessageId(randomId),
                        userId,
                        diagramId,
                        state._id,
                        param.message
                      )
                    )
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
        val startState = stateRepo.getStartState(diagramId)
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
      _ <- stateRepo.insert(state)
    } yield AddState.Result(state._id)

  }

  def recordState(message: MessageEntity)(implicit
      ec: ExecutionContext
  ) {
    messageRepo.insert(message)
  }
  def setState(state: CacheState) {
    redisRepo.set(
      state.userId,
      state.toJson
    )
  }

  def getState(stateId: StateId)(implicit
      ec: ExecutionContext
  ): Future[Option[StateEntity]] = {
    stateRepo.getOne(stateId)

  }

  def getAll()(implicit
      ec: ExecutionContext
  ): Future[List[StateEntity]] = {
    stateRepo.getAll()
  }

  def getComplaints()(implicit
      ec: ExecutionContext
  ): Future[AllMessage.Result] = {
    for {
      messages <- messageRepo.getAllByStateId(StateId("3"))
    } yield AllMessage.Result(messages)

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
      StateId("1"),
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
      StateId("2"),
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
      StateId("3"),
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
      StateId("4"),
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
      StateId("5"),
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
