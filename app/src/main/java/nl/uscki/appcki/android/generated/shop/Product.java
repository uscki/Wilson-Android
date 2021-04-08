package nl.uscki.appcki.android.generated.shop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Product implements IWilsonBaseItem {

    private final int rnd = new Random().nextInt();

    @Expose
    @SerializedName("id")
    public Integer id;
    @Expose
    @SerializedName("image")
    public Integer image;
    @Expose
    @SerializedName("price")
    public Double price;
    @Expose
    @SerializedName("stock")
    public Integer stock;
    @Expose
    @SerializedName("title")
    public String title;

    @Override
    public Integer getId() {
        return id;
    }

    public int getRnd() {
        return rnd;
    }
}
