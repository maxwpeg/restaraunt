package org.komarov.classes;

public class OrderThread extends Thread{
  private final Order order;
  private final Order_System orderSystem;

  public OrderThread(Order order, Order_System orderSystem) {
    this.order = order;
    this.orderSystem = orderSystem;
  }
  @Override
  public void run() {
    orderSystem.processOrder(order);
  }
}
