package subsystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        stock.put("LAPTOP-01", 5);
        stock.put("MOUSE-02",20);
        stock.put("KEYBOARD-03", 2);
    }

    public boolean hasEnoughStock(String productId, int quantity){
        int available = stock.getOrDefault(productId, 0);
        System.out.println("[Inventory] Checking stock for " + productId + ": requested=" + quantity + ", available=" + available);
        return available >= quantity ;
    }

    public void reserveStock(String productId, int quantity) {
        int available = stock.getOrDefault(productId,0);
        if(available < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
        stock.put(productId, available - quantity);
        System.out.println("[Inventory] Reserved " + quantity + " unit(s) of " + productId + ". Remaining: " + stock.get(productId));
    }

    public void releaseStock(String productId,int quantity) {
        int available = stock.getOrDefault(productId, 0);
        stock.put(productId, available + quantity);
        System.out.println("[Inventory] Released " + quantity + " unit(s) of " + productId + ". Remaining: " + stock.get(productId)) ;
    }
}