package c.plus.plan.shorts.network;

import java.util.List;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.PageResult;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.PurchaseRecord;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.entity.request.RequestPurchase;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.entity.response.PurchaseResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by fanwei on 7/17/24
 */
public interface HttpService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/api/config/price")
    Call<DataResult<List<DramaPrice>>> requestPriceList();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("jowo-server/api/purchase/check")
    Call<DataResult<PurchaseResponse>> postPurchaseCheck(@Body RequestPurchase requestPurchase);


    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/api/purchase/record")
    Call<DataResult<PageResult<List<PurchaseRecord>>>> requestPurchaseRecordList(@Query("cursorId") int cursorId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/api/purchase/product")
    Call<DataResult<List<PurchaseProduct>>> requestPurchaseProductList();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("jowo-server/api/consumption/unlockDrama")
    Call<DataResult<UnlockDramaRecord>> postUnlockDrama(@Body RequestUnlockDrama requestUnlockDrama);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("jowo-server/api/consumption/record")
    Call<DataResult<PageResult<List<UnlockDramaRecord>>>> requestUnlockDramaRecordList(@Query("cursorId") int cursorId);
}
