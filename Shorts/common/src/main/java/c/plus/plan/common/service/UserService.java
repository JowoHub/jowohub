package c.plus.plan.common.service;

import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.User;

import androidx.lifecycle.LiveData;

/**
 * Created by fanwei on 2/14/22
 */
public interface UserService {
    void refreshToken();
    void getCurrentUser();

    LiveData<User> getCurrentUserLV();
    LiveData<User> getUserById(long id);

    LiveData<DataResult<User>> requestMyUserInfo();

    LiveData<DataResult> logout();
    LiveData<DataResult> logoff();

    void updateUserCoin(int count);
}
