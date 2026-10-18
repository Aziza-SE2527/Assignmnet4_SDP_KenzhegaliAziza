package com.patterns.facade.facade;

import com.patterns.facade.model.Customer;
import com.patterns.facade.model.OrderItem;
import com.patterns.facade.model.OrderResult;
import com.patterns.facade.subsystem.InventoryService;
import com.patterns.facade.subsystem.NotificationService;
import com.patterns.facade.subsystem.PaymentProcessor;
import com.patterns.facade.subsystem.ShippingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderFacade {
    private final InventoryService inventoryService;
    private final PaymentProcessor paymentProcessor;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    private Map<String, ActiveOrderRecord> activeOrders = new HashMap<>();

    private record ActiveOrderRecord(
            Customer customer,
            List<OrderItem> items,
            String transactionId,
            String trackingNumber,
            double totalAmount
    ) {}

    public OrderFacade() {
        this.inventoryService = new InventoryService();
        this.paymentProcessor = new PaymentProcessor();
        this.shippingService = new ShippingService();
        this.notificationService = new NotificationService();
    }

    public OrderFacade(InventoryService inventoryService, PaymentProcessor paymentProcessor, ShippingService shippingService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.paymentProcessor = paymentProcessor;
        this.shippingService = shippingService;
        this.notificationService = notificationService;
    }

    public OrderResult placeOrder(List<OrderItem> cart, Customer customer) {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println("\n--- Processing Order " + orderId + " for " + customer.name() + " ---");

        for(OrderItem item : cart) {
            if (!inventoryService.hasEnoughStock(item.productId(), item.quantity())) {
                String reason = "Out of stock: " + item.productName();
                notificationService.sendOrderFailure(customer.email(), reason);
                return new OrderResult(false, orderId, null, null, 0.0, reason);
            }
        }

        for (OrderItem item : cart){
            inventoryService.reserveStock(item.productId(),item.quantity());
        }

        double totalAmount = calculateTotal(cart);
        String transactionId = paymentProcessor.charge(customer.name(), customer.accountBalance(), totalAmount);

        if(transactionId == null) {
            rollbackInventory(cart);
            String reason = "Payment declined (insufficient balance)";
            notificationService.sendOrderFailure(customer.email(), reason);
            return new OrderResult(false, orderId, null,null, totalAmount, reason);
        }

        String trackingNumber = shippingService.createShipment(orderId, customer.shippingAddress());
        if (trackingNumber == null){
            paymentProcessor.refund(transactionId);
            rollbackInventory(cart);
            String reason = "Invalid shipping address";
            notificationService.sendOrderFailure(customer.email(), reason);
            return new OrderResult(false, orderId, transactionId, null, totalAmount, reason);
        }

        activeOrders.put(orderId, new ActiveOrderRecord(customer, cart, transactionId, trackingNumber, totalAmount));
        notificationService.sendOrderConfirmation(customer.email(), orderId, trackingNumber, totalAmount);

        return new OrderResult(true, orderId, transactionId, trackingNumber, totalAmount, "Order placed successfully");
    }

    public boolean cancelOrder(String orderId){
        System.out.println("\n--- Cancelling Order " + orderId + " ---");
        ActiveOrderRecord record = activeOrders.remove(orderId);
        if(record == null) {
            System.out.println("[Facade] Order " + orderId + " not found.");
            return false;
        }

        shippingService.cancelShipment(record.trackingNumber());
        paymentProcessor.refund(record.transactionId());
        rollbackInventory(record.items());
        notificationService.sendOrderCancellation(record.customer().email(), orderId);
        return true;
    }

    private double calculateTotal(List<OrderItem> cart) {
        double sum = 0;
        for (OrderItem item : cart) {
            sum += item.totalPrice();
        }
        return sum;
    }

    private void rollbackInventory(List<OrderItem> cart){
        for (OrderItem item : cart) {
            inventoryService.releaseStock(item.productId(), item.quantity());
        }
    }
}