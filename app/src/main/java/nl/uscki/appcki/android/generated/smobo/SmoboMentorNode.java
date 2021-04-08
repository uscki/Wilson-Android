package nl.uscki.appcki.android.generated.smobo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.uscki.appcki.android.generated.organisation.PersonName;

/**
 * Created by peter on 3/4/17.
 */

public class SmoboMentorNode {
    @Expose
    @SerializedName("children")
    List<PersonName> children;
    @Expose
    @SerializedName("parents")
    List<PersonName> parents;
    @Expose
    @SerializedName("subnodes")
    List<SmoboMentorNode> subnodes;

    public List<PersonName> getChildren() {
        return children;
    }

    public void setChildren(List<PersonName> children) {
        this.children = children;
    }

    public List<PersonName> getParents() {
        return parents;
    }

    public void setParents(List<PersonName> parents) {
        this.parents = parents;
    }

    public List<SmoboMentorNode> getSubnodes() {
        return subnodes;
    }

    public void setSubnodes(List<SmoboMentorNode> subnodes) {
        this.subnodes = subnodes;
    }
}