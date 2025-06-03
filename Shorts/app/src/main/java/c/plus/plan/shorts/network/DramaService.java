package c.plus.plan.shorts.network;

import androidx.lifecycle.LiveData;

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

public interface DramaService {
    LiveData<DataResult<List<DramaPrice>>> requestPriceList();
    LiveData<DramaPrice> getDramaPrice();
    LiveData<DataResult<PurchaseResponse>> postPurchaseCheck(RequestPurchase requestPurchase);
    LiveData<DataResult<PageResult<List<PurchaseRecord>>>> requestPurchaseRecordList(int cursorId);
    LiveData<DataResult<List<PurchaseProduct>>> requestPurchaseProductList();
    LiveData<List<PurchaseProduct>> getPurchaseProductList();
    LiveData<DataResult<UnlockDramaRecord>> postUnlockDrama(RequestUnlockDrama requestUnlockDrama);
    LiveData<DataResult<PageResult<List<UnlockDramaRecord>>>> requestUnlockDramaRecordList(int cursorId);
}
