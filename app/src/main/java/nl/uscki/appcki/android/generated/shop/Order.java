package nl.uscki.appcki.android.generated.shop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class Order {
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
    DateTime date;
}
