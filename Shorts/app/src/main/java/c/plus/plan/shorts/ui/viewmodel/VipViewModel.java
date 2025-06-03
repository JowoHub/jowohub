package c.plus.plan.shorts.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.shorts.entity.PurchaseProduct;
import c.plus.plan.shorts.entity.request.RequestPurchase;
import c.plus.plan.shorts.entity.response.PurchaseResponse;
import c.plus.plan.shorts.network.DramaServiceImpl;

public class VipViewModel extends ViewModel {
    private final DramaServiceImpl mDramaService;

    public VipViewModel() {
        mDramaService = DramaServiceImpl.get();
        requestPurchaseProductList();
    }

    public LiveData<DataResult<List<PurchaseProduct>>> requestPurchaseProductList(){
        return mDramaService.requestPurchaseProductList();
    }

    public LiveData<List<PurchaseProduct>> getPurchaseProductList(){
        return mDramaService.getPurchaseProductList();
    }

    public LiveData<DataResult<PurchaseResponse>> postPurchaseCheck(RequestPurchase requestPurchase){
        return mDramaService.postPurchaseCheck(requestPurchase);
    }
}
