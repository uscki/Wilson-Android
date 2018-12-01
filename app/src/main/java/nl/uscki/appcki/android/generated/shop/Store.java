package nl.uscki.appcki.android.generated.shop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

public class Store implements IWilsonBaseItem {
    @Expose
    @SerializedName("id")
    public Integer id;
    @Expose
    @SerializedName("image")
    public Integer image;
    @Expose
    @SerializedName("description")
    public String description;
    @Expose
    @SerializedName("title")
    public String title;

    @Override
    public Object getId() {
        return id;
    }
}
