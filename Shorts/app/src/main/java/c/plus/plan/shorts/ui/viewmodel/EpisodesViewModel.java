package c.plus.plan.shorts.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.jowosdk.entity.UserUnlockEpisodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.network.DramaServiceImpl;

public class EpisodesViewModel extends ViewModel {
    private final DramaServiceImpl mDramaService;
    private UserUnlockEpisodes userUnlockEpisodes;
    private int currentNum;

    public EpisodesViewModel() {
        mDramaService = DramaServiceImpl.get();
    }

    public UserUnlockEpisodes getUserUnlockEpisodes() {
        return userUnlockEpisodes;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public void getData(String dramaId, Callback<UserUnlockEpisodes> callback){
        JOWOSdk.lastUserDrama(dramaId, result -> {
            if(result.getData() != null){
                userUnlockEpisodes = result.getData();
                callback.result(result);
            } else {
                JOWOSdk.fetchUserDrama(dramaId, result1 -> {
                    if(result1.getCode() == Result.Code.OK){
                        userUnlockEpisodes = result1.getData();
                    }
                    callback.result(result1);
                });
            }
        });

    }

    public LiveData<DramaPrice> getDramaPrice() {
        return mDramaService.getDramaPrice();
    }

    public LiveData<DataResult<UnlockDramaRecord>> postUnlockDrama(RequestUnlockDrama requestUnlockDrama){
        return mDramaService.postUnlockDrama(requestUnlockDrama);
    }
}
