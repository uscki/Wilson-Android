package nl.uscki.appcki.android.api;

import java.util.List;

import nl.uscki.appcki.android.generated.common.Pageable;
import nl.uscki.appcki.android.generated.shop.Order;
import nl.uscki.appcki.android.generated.shop.Product;
import nl.uscki.appcki.android.generated.shop.Store;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShopService {
    @GET("shop/stores/")
    Call<List<Store>> getStores();

    @GET("shop/stores/{store}/products")
    Call<Pageable<Product>> getProductsForStore(@Path("store") Integer store);

    @GET("shop/orders/")
    Call<List<Order>> getOrders();

    @POST("shop/orders/new")
    Call<Boolean> placeOrder(@Query("id") Integer productId, @Query("amount") Integer amount);
}
