package c.plus.plan.common.network;

import java.util.HashMap;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.User;
import c.plus.plan.common.entity.request.RequestDeviceInfo;
import c.plus.plan.common.entity.response.UserTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by fanwei on 2/14/22
 */
public interface HttpUserService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("jowo-server/api/account/token/login")
    Call<DataResult<UserTokenResponse>> createToken(@Body RequestDeviceInfo deviceInfo);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("jowo-server/api/account/token/refresh")
    Call<DataResult<UserTokenResponse>> refreshToken(@Body RequestDeviceInfo deviceInfo);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/v1/token/logout")
    Call<DataResult<UserTokenResponse>> logout();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/v1/token/logoff")
    Call<DataResult<UserTokenResponse>> logoff();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/api/account/info")
    Call<DataResult<User>> getMyUserInfo();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT("jowo-server/v1/user/")
    Call<DataResult<User>> updateUserInfo(@Body HashMap map);
}
