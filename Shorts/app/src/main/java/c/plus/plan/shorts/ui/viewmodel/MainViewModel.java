package c.plus.plan.shorts.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.network.DramaServiceImpl;

public class MainViewModel extends ViewModel {
    private final DramaServiceImpl mDramaService;

    public MainViewModel() {
        mDramaService = DramaServiceImpl.get();

        mDramaService.requestPriceList();
        mDramaService.requestPurchaseProductList();
    }

    public LiveData<DramaPrice> getDramaPrice() {
        return mDramaService.getDramaPrice();
    }

    public LiveData<DataResult<UnlockDramaRecord>> postUnlockDrama(RequestUnlockDrama requestUnlockDrama){
        return mDramaService.postUnlockDrama(requestUnlockDrama);
    }
}
