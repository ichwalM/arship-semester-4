package model;

public class Product {
    private int product_id;
    private String product_name;
    private double product_harga;

    public Product(int product_id, String product_name, double product_harga) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_harga = product_harga;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public double getProduct_harga() {
        return product_harga;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_harga(double product_harga) {
        this.product_harga = product_harga;
    }
    
    
}
