package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShopPreferenceHelper {
    private Context context;
    private SharedPreferences preferences;

    public ShopPreferenceHelper(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get the ID of the shop that should be opened. This is either the default shop as set in
     * the preferences, or the last opened shop. If no shop has ever been opened, -1 is returned,
     * and the first shop from the ShopService should be used
     *
     * @return integer containing shop ID, or negative if no shop has been selected to open
     * (this can also be if the user always wants to choose the shop)
     */
    public int getShop() {
        int defaultShop = Integer.parseInt(preferences.getString("preference_default_shop", "-3"));
        if(defaultShop == -1) {
            defaultShop = preferences.getInt("last_opened_shop", -1);
        }
        return defaultShop;
    }

    /**
     * Set the ID of the shop that was last opened
     * @param shopId    ID of the shop that was last opened
     */
    public void setLastShop(int shopId){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_opened_shop", shopId);
        editor.apply();
    }
}
