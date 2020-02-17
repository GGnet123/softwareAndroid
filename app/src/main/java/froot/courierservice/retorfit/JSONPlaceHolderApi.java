package froot.courierservice.retorfit;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    ////get order list

    @GET("runners/runner-profile")
    public Call<getProfile> getProfile(
            @Header("x-auth-token") String token
    );

    @GET("runners/orders")
    public Call<getJson> getOrders(
            @Header("x-auth-token") String token,
            @Query("page") int page
    );
    ///login / get-token
    @POST("runners/login")
    Call<Post> postLogin(@Body Post post);

    @POST("runners/location")
    Call<PostLocation> postLocation(
            @Header("x-auth-token") String token,
            @Body PostLocation post
    );

    @POST("runners/device-token")
    Call<PostToken> postDeviceToken(@Header("x-auth-token") String token,
                                    @Body PostToken postToken);
    @POST("runners/order-change-status")
    Call<PostOrder> postOrderStatus(@Header("x-auth-token") String token,
                                    @Body PostOrder order);
    @GET("runners/is-busy")
    public Call<getIsBusy> isBusy(
            @Header("x-auth-token") String token
    );
}