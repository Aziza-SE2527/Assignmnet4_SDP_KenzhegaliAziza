package model;

public record OrderResult(
        boolean success,
        String orderId,
        String transactionId,
        String trackingNumber,
        double totalAmount,
        String message
)
{

}