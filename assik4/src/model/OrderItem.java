package model;

public record OrderItem(String productId, String productName,int quantity, double unitPrice)
{
    public double totalPrice()
    {
        return quantity * unitPrice;
    }
}