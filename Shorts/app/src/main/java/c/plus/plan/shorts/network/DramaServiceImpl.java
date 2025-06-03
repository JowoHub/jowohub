package c.plus.plan.shorts.network;

import androidx.lifecycle.LiveData;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.PageResult;
import c.plus.plan.common.network.ApiManager;
import c.plus.plan.common.utils.SingleLiveEvent;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.PurchaseRecord;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.entity.request.RequestPurchase;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.entity.response.PurchaseResponse;
import c.plus.plan.shorts.event.CoinUnlockEvent;
import c.plus.plan.shorts.manager.ShortsDataBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaServiceImpl implements DramaService {
    private static DramaServiceImpl service;
    private final HttpService mHttpService;

    public static synchronized DramaServiceImpl get() {
        if(service == null){
            service = new DramaServiceImpl();
        }
        return service;
    }


    private DramaServiceImpl() {
        mHttpService = (HttpService) ApiManager.get().getService(HttpService.class);
    }

    @Override
    public LiveData<DataResult<List<DramaPrice>>> requestPriceList() {
        SingleLiveEvent<DataResult<List<DramaPrice>>> result = new SingleLiveEvent<>();
        Call<DataResult<List<DramaPrice>>> call = mHttpService.requestPriceList();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<List<DramaPrice>>> call, Response<DataResult<List<DramaPrice>>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                    List<DramaPrice> dramaPriceList = response.body().getData();
                    if(CollectionUtils.isNotEmpty(dramaPriceList)){
                        ShortsDataBase.dbWriteExecutor.execute(() -> {
                            ShortsDataBase.get().dramaPriceDao().insertAll(dramaPriceList);
                        });
                    }
                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<List<DramaPrice>>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }

    @Override
    public LiveData<DramaPrice> getDramaPrice() {
        SingleLiveEvent<DramaPrice> result = new SingleLiveEvent<>();
        ShortsDataBase.dbWriteExecutor.execute(() -> {
            DramaPrice price = ShortsDataBase.get().dramaPriceDao().getCurrent();
            ViewUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.setValue(price);
                }
            });
        });
        return result;
    }

    @Override
    public LiveData<DataResult<PurchaseResponse>> postPurchaseCheck(RequestPurchase requestPurchase) {
        SingleLiveEvent<DataResult<PurchaseResponse>> result = new SingleLiveEvent<>();
        Call<DataResult<PurchaseResponse>> call = mHttpService.postPurchaseCheck(requestPurchase);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<PurchaseResponse>> call, Response<DataResult<PurchaseResponse>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<PurchaseResponse>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }

    @Override
    public LiveData<DataResult<PageResult<List<PurchaseRecord>>>> requestPurchaseRecordList(int cursorId) {
        SingleLiveEvent<DataResult<PageResult<List<PurchaseRecord>>>> result = new SingleLiveEvent<>();
        Call<DataResult<PageResult<List<PurchaseRecord>>>> call = mHttpService.requestPurchaseRecordList(cursorId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<PageResult<List<PurchaseRecord>>>> call, Response<DataResult<PageResult<List<PurchaseRecord>>>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());

                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<PageResult<List<PurchaseRecord>>>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }

    @Override
    public LiveData<DataResult<List<PurchaseProduct>>> requestPurchaseProductList() {
        SingleLiveEvent<DataResult<List<PurchaseProduct>>> result = new SingleLiveEvent<>();
        Call<DataResult<List<PurchaseProduct>>> call = mHttpService.requestPurchaseProductList();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<List<PurchaseProduct>>> call, Response<DataResult<List<PurchaseProduct>>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                    List<PurchaseProduct> purchaseProducts = response.body().getData();
                    if(CollectionUtils.isNotEmpty(purchaseProducts)){
                        ShortsDataBase.dbWriteExecutor.execute(() -> {
                            ShortsDataBase.get().purchaseProductDao().update(purchaseProducts);
                        });
                    }
                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<List<PurchaseProduct>>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }

    @Override
    public LiveData<List<PurchaseProduct>> getPurchaseProductList() {
        SingleLiveEvent<List<PurchaseProduct>> result = new SingleLiveEvent<>();
        ShortsDataBase.dbWriteExecutor.execute(() -> {
            List<PurchaseProduct> list = ShortsDataBase.get().purchaseProductDao().selectAll();
            ViewUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.setValue(list);
                }
            });
        });
        return result;
    }

    @Override
    public LiveData<DataResult<UnlockDramaRecord>> postUnlockDrama(RequestUnlockDrama requestUnlockDrama) {
        SingleLiveEvent<DataResult<UnlockDramaRecord>> result = new SingleLiveEvent<>();
        Call<DataResult<UnlockDramaRecord>> call = mHttpService.postUnlockDrama(requestUnlockDrama);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<UnlockDramaRecord>> call, Response<DataResult<UnlockDramaRecord>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                    EventBus.getDefault().post(new CoinUnlockEvent(true));
                } else {
                    result.setValue(DataResult.generateFailResult());
                    EventBus.getDefault().post(new CoinUnlockEvent(false));
                }
            }

            @Override
            public void onFailure(Call<DataResult<UnlockDramaRecord>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
                EventBus.getDefault().post(new CoinUnlockEvent(false));
            }
        });
        return result;
    }

    @Override
    public LiveData<DataResult<PageResult<List<UnlockDramaRecord>>>> requestUnlockDramaRecordList(int cursorId) {
        SingleLiveEvent<DataResult<PageResult<List<UnlockDramaRecord>>>> result = new SingleLiveEvent<>();
        Call<DataResult<PageResult<List<UnlockDramaRecord>>>> call = mHttpService.requestUnlockDramaRecordList(cursorId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<PageResult<List<UnlockDramaRecord>>>> call, Response<DataResult<PageResult<List<UnlockDramaRecord>>>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<PageResult<List<UnlockDramaRecord>>>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }
}
