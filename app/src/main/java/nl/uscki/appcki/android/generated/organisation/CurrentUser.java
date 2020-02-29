package nl.uscki.appcki.android.generated.organisation;

import com.google.gson.annotations.Expose;

/**
 * This class corresponds to nl.uscki.api.web.rest.beans.organization.CurrentUserBean in the
 * B.A.D.W.O.L.F. API and is only used to store information relating to the currently logged in
 * user.
 */
public class CurrentUser extends Person {

    @Expose
    private boolean forum_new_posts_first;

    public boolean isForum_new_posts_first() {
        return forum_new_posts_first;
    }

    public void setForum_new_posts_first(boolean forum_new_posts_first) {
        this.forum_new_posts_first = forum_new_posts_first;
    }
}
