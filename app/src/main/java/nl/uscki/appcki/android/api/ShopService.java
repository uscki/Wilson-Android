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
    @GET("shops/")
    Call<List<Store>> getStores();

    @GET("shops/{store}/products/")
    Call<Pageable<Product>> getProductsForStore(@Path("store") Integer store, @Query("page") Integer page, @Query("size") Integer size);

    @GET("shops/orders/")
    Call<Pageable<Order>> getOrders(@Query("page") Integer page, @Query("size") Integer size);

    @POST("shops/{storeId}/products/{productId}/order")
    Call<Boolean> placeOrder(@Path("storeId") Integer storeId, @Path("productId") Integer productId, @Query("amount") Integer amount);
}
