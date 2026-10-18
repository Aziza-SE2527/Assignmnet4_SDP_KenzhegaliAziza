package subsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShippingService {
    private Map<String, String> activeShipments = new HashMap<>();

    public String createShipment(String orderId, String address) {
        if(address == null || address.isBlank()){
            System.out.println("[Shipping] Error: Invalid shipping address");
            return null;
        }
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        activeShipments.put(trackingNumber,orderId);
        System.out.println("[Shipping] Shipment created for Order " + orderId + " to " + address + " (Tracking: " + trackingNumber + ")");
        return trackingNumber;
    }

    public boolean cancelShipment(String trackingNumber) {
        String orderId = activeShipments.remove(trackingNumber);
        if (orderId != null) {
            System.out.println("[Shipping] Cancelled shipment " + trackingNumber + " for Order " + orderId);
            return true;
        }
        return false ;
    }
}