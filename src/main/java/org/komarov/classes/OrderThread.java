package org.komarov.classes;

public class OrderThread extends Thread {
    private final int orderIndex;
    private final OrderSystem orderSystem;

    public OrderThread(int index, OrderSystem orderSystem) {
        this.orderIndex = index;
        this.orderSystem = orderSystem;
    }

    @Override
    public void run() {
        orderSystem.processOrder(orderIndex);
    }
}
