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
}
