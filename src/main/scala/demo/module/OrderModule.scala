package demo.module

import demo.service.OrderService
import demo.manager.OrderManager

class OrderModule() {
  def build(): OrderService = {
    val manager = new OrderManager()
    val service = new OrderService(manager)
    service
  }
}
