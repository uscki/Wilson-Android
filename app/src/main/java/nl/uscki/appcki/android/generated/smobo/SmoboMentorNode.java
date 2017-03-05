package nl.uscki.appcki.android.generated.smobo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.uscki.appcki.android.generated.organisation.PersonSimpleName;

/**
 * Created by peter on 3/4/17.
 */

public class SmoboMentorNode {
    @Expose
    @SerializedName("children")
    List<PersonSimpleName> children;
    @Expose
    @SerializedName("parents")
    List<PersonSimpleName> parents;
    @Expose
    @SerializedName("subnodes")
    List<SmoboMentorNode> subnodes;

    public List<PersonSimpleName> getChildren() {
        return children;
    }

    public void setChildren(List<PersonSimpleName> children) {
        this.children = children;
    }

    public List<PersonSimpleName> getParents() {
        return parents;
    }

    public void setParents(List<PersonSimpleName> parents) {
        this.parents = parents;
    }

    public List<SmoboMentorNode> getSubnodes() {
        return subnodes;
    }

    public void setSubnodes(List<SmoboMentorNode> subnodes) {
        this.subnodes = subnodes;
    }
}
