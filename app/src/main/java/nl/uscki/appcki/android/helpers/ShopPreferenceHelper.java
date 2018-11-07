package nl.uscki.appcki.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import nl.uscki.appcki.android.generated.shop.Store;

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

    /**
     * Update whether a confirm dialog should be shown before a shop order is finalized
     * @param showConfirm   True iff a confirm dialog should be shown before a shop order is
     *                      finalized
     */
    public void setShowConfirm(boolean showConfirm) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("preference_shop_confirm_before_order", showConfirm);
        editor.apply();
    }

    /**
     * Get the preference value for whether a confirm dialog should be shown before a shop
     * order is finalized
     * @return  Boolean
     */
    public boolean getShowConfirm() {
        return preferences.getBoolean("preference_shop_confirm_before_order", true);
    }

    public void updateShops(String jsonShopList) {
        SharedPreferences.Editor editor = preferences.edit();
                editor.putString("cached_store_set", jsonShopList);
        editor.apply();
    }

    public List<Store> getLastShopList() {
        String json = preferences.getString("cached_store_set", null);
        if (json == null) {
            return null;
        } else {
            Gson gson = new Gson();
            Type storeListType = new TypeToken<ArrayList<Store>>() { }.getType();
            return gson.fromJson(json, storeListType);
        }
    }

    public Store getStore(int id) {
        List<Store> lastShopList = getLastShopList();
        if (lastShopList == null) return null;
        for (Store store : lastShopList) {
            if (store.id.equals(id)) {
                return store;
            }
        }
        return null;
    }

    private class StoreNameIdPair implements Serializable {
        private int id;
        private String name;

        StoreNameIdPair(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String encode() throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(this);
            oos.close();
            return os.toString();
        }

    }
}
