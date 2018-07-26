package nl.uscki.appcki.android.generated.agenda;

import com.google.gson.annotations.Expose;

import nl.uscki.appcki.android.generated.IWilsonBaseItem;

/**
 * Created by peter on 11/16/16.
 */
public class AgendaCategory implements IWilsonBaseItem{
    @Expose
    private Integer id;
    @Expose
    private String menutitle;
    @Expose
    private String singular;
    @Expose
    private String plural;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMenutitle() {
        return menutitle;
    }

    public void setMenutitle(String menutitle) {
        this.menutitle = menutitle;
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }
}
