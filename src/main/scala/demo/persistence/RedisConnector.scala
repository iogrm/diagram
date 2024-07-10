package demo.persistence

import com.redis.RedisClient

class RedisConnector() {

  val db: RedisClient = {
    println("Redis Connecting...")
    val r = new RedisClient("localhost", 6379)
    r
  }

}
