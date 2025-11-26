package org.example.springboot.enumClass;

/**
 * 订单状态枚举
 * @author system
 */
public enum OrderStatus {
    UNPAID("待付款"),
    PAID("待发货"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断订单是否可以支付
     */
    public boolean canPay() {
        return this == UNPAID;
    }

    /**
     * 判断订单是否可以取消
     */
    public boolean canCancel() {
        return this == UNPAID;
    }

    /**
     * 判断订单是否可以发货
     */
    public boolean canShip() {
        return this == PAID;
    }

    /**
     * 判断订单是否可以确认收货
     */
    public boolean canComplete() {
        return this == SHIPPED;
    }
}
