package nl.uscki.appcki.android.generated.shop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Order implements IWilsonBaseItem {
    @Expose
    @SerializedName("id")
    Integer id;
    @Expose
    @SerializedName("price")
    Double price;
    @Expose
    @SerializedName("product")
    String product;
    @Expose
    @SerializedName("date")
    Long date;

    private int amount = 1;

    public Integer getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public String getProduct() {
        return product;
    }

    public Long getDate() {
        return date;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }

    public void increaseAmount(){
        this.amount++;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean equals(Order obj) {
        return this.product.equals(obj.getProduct()) && this.date.equals(obj.date) && this.price.equals(obj.price);
    }
}
