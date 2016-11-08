
package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Committee {

    @Expose
    private Integer id;
    @Expose
    private Group grp;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The grp
     */
    public Group getGroup() {
        return grp;
    }

    /**
     * 
     * @param grp
     *     The grp
     */
    public void setGroup(Group grp) {
        this.grp = grp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
