package c.plus.plan.common.ui.viewmodel;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.User;
import c.plus.plan.common.service.UserService;
import c.plus.plan.common.service.impl.UserServiceImpl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by fanwei on 2/14/22
 */
public class UserViewModel extends ViewModel {
    UserService userService;

    public UserViewModel() {
        userService = new UserServiceImpl();
    }

    public void refreshToken(){
        userService.refreshToken();
    }

    public void getCurrentUser(){
        userService.getCurrentUser();
    }

    public LiveData<DataResult> logout(){
        return userService.logout();
    }

    public LiveData<DataResult> logoff(){
        return userService.logoff();
    }

    public LiveData<DataResult<User>> requestMyUserInfo() {
        return userService.requestMyUserInfo();
    }

    public void updateUserCoin(int count) {
        userService.updateUserCoin(count);
    }
}
