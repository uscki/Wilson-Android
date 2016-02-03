
package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Sort {

    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("property")
    @Expose
    private String property;
    @SerializedName("ignoreCase")
    @Expose
    private boolean ignoreCase;
    @SerializedName("nullHandling")
    @Expose
    private String nullHandling;
    @SerializedName("ascending")
    @Expose
    private boolean ascending;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
