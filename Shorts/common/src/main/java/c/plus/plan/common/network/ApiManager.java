package c.plus.plan.common.network;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import c.plus.plan.common.NativeManager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fanwei on 1/23/22
 */
public class ApiManager<T> {
    private static ApiManager instance;
    private HashMap<Class<T>, T> mServiceMap = new HashMap<>();

    private ApiManager() {
    }

    public synchronized static ApiManager get() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public T getService(Class<T> tClass) {
        if (mServiceMap.get(tClass) == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(new ApiEncryptInterceptor());
            OkHttpClient client = httpClient
//                    .readTimeout(1000, TimeUnit.MILLISECONDS)
//                    .writeTimeout(1000, TimeUnit.MILLISECONDS)
//                    .callTimeout(1000, TimeUnit.MILLISECONDS)
//                    .connectTimeout(1000, TimeUnit.MILLISECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:SS")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(instance.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
            mServiceMap.put(tClass, retrofit.create(tClass));
        }
        return mServiceMap.get(tClass);
    }

    private String getBaseUrl() {
        if (AppUtils.isAppDebug()) {
            return NativeManager.get().getUrlByKey(NativeManager.TEST_HOST);
        } else {
            return NativeManager.get().getUrlByKey(NativeManager.PRODUCT_HOST);
        }
    }
}
