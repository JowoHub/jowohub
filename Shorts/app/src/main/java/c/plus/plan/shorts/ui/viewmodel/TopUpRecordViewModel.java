package c.plus.plan.shorts.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.PageResult;
import c.plus.plan.shorts.entity.PurchaseRecord;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.network.DramaServiceImpl;

public class TopUpRecordViewModel extends ViewModel {
    private final DramaServiceImpl mDramaService;

    public TopUpRecordViewModel() {
        mDramaService = DramaServiceImpl.get();
    }

    public LiveData<DataResult<PageResult<List<PurchaseRecord>>>> requestPurchaseRecordList(int cursorId){
        return mDramaService.requestPurchaseRecordList(cursorId);
    }

    public LiveData<DataResult<PageResult<List<UnlockDramaRecord>>>> requestUnlockDramaRecordList(int cursorId) {
        return mDramaService.requestUnlockDramaRecordList(cursorId);
    }
}
