package subsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentProcessor {
    private Map<String, Double> processedPayments = new HashMap<>();

    public String charge(String customerName, double balance,double amount) {
        System.out.println("[Payment] Charging $" + amount + " to " + customerName);
        if (balance < amount){
            System.out.println("[Payment] Failed: Insufficient balance ($" + balance + ")");
            return null;
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        processedPayments.put(transactionId, amount);
        System.out.println("[Payment] Success! Transaction ID: " + transactionId);
        return transactionId;
    }

    public boolean refund(String transactionId){
        Double amount = processedPayments.remove(transactionId);
        if(amount != null) {
            System.out.println("[Payment] Refunded $" + amount + " for TXN: " + transactionId);
            return true;
        }
        System.out.println("[Payment] Refund failed: TXN " + transactionId + " not found");
        return false;
    }
}