package subsystem;

public class NotificationService {

    public void sendOrderConfirmation(String email, String orderId,String trackingNumber, double totalAmount) {
        System.out.println("[Notification] Email to " + email + ": Order " + orderId + " confirmed. Total: $" + totalAmount + ", Tracking: " + trackingNumber);
    }

    public void sendOrderFailure(String email, String reason){
        System.out.println("[Notification] Email to " + email + ": Order failed - " + reason);
    }

    public void sendOrderCancellation(String email,String orderId) {
        System.out.println("[Notification] Email to " + email + ": Order " + orderId + " cancelled and refunded.");
    }
}