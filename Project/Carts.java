package Project;

public class Carts {
    private String name, type;
    private int price, quantity, subtotal;

    public Carts(String name, String type, int price, int quantity, int subtotal){
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;}

    public String getName() {
        return name;}

    public String getType() {
        return type;}

    public int getPrice() {
        return price;}

    public int getQuantity() {
        return quantity;}

    public int getSubtotal() {
        return subtotal;}}
