package c.plus.plan.common.network;

import com.blankj.utilcode.util.GsonUtils;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Joey.zhao
 * @date 2025/3/22 20:17
 * @description
 */
public class FakeResponse {
    public static Response fakeResponse(Request request,String json) {
        return new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_2)
                .request(request)
                .code(200)
                .body(ResponseBody.create(MediaType.get("application/json"), json))
                .build();
    }
}
