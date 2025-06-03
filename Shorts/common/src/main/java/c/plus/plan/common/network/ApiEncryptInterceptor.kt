package c.plus.plan.common.network

import c.plus.plan.common.NativeManager
import c.plus.plan.common.constants.Constants
import c.plus.plan.common.entity.BaseHeader
import c.plus.plan.common.entity.Current
import c.plus.plan.common.entity.PageResult
import c.plus.plan.common.entity.User
import c.plus.plan.common.entity.response.UserTokenResponse
import c.plus.plan.common.utils.AesUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by fanwei on 1/23/22
 */
class ApiEncryptInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        val original = chain.request()
        try {
            if (original.url.toString().contains("jowo-server/api/account/token/login")) {
                val userTokenResponse = UserTokenResponse()
                val user = User()
                user.id = 100001
                user.nickname = "JOWO"
                user.avatar = "https://avatars.githubusercontent.com/u/1437467?v=4"
                user.gender = 1
                userTokenResponse.user = user
                userTokenResponse.token = "token_test"
                userTokenResponse.expireAt = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365
                userTokenResponse.user = user
                val jsonObject = HashMap<String, Any>()
                jsonObject["data"] = userTokenResponse
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else if (original.url.toString()
                    .contains("jowo-server/api/purchase/record") || original.url.toString()
                    .contains("jowo-server/api/consumption/record")
            ) {
                val data: PageResult<*> = PageResult<Any?>()
                data.totalCount = 0
                val jsonObject = HashMap<String, Any>()
                jsonObject["data"] = data
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else if (original.url.toString().contains("jowo-server/api/config/price")) {
                val jsonObject = HashMap<String, Any>()
                val data = ArrayList<HashMap<String, Any>>()
                data.add(HashMap<String, Any>().apply {
                    put("country", "CN")
                    put("dramaLevelCoin", HashMap<String, Any>().apply {
                        put("S", 1000)
                        put("SS", 3000)
                        put("SS", 6000)
                    })
                })
                data.add(HashMap<String, Any>().apply {
                    put("country", "US")
                    put("dramaLevelCoin", HashMap<String, Any>().apply {
                        put("S", 1000)
                        put("SS", 3000)
                        put("SS", 6000)
                    })
                })
                jsonObject["data"] = data
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else if (original.url.toString()
                    .contains("jowo-server/api/account/token/refresh")
            ) {
                val userTokenResponse = UserTokenResponse()
                val user = User()
                user.id = 100001
                user.nickname = "JOWO"
                user.avatar = "https://avatars.githubusercontent.com/u/1437467?v=4"
                user.gender = 1
                userTokenResponse.user = user
                userTokenResponse.token = "token_test"
                userTokenResponse.expireAt = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365
                userTokenResponse.user = user
                val jsonObject = HashMap<String, Any>()
                jsonObject["data"] = userTokenResponse
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else if (original.url.toString().contains("jowo-server/api/account/info")) {
                val user = User()
                user.id = 100001
                user.nickname = "JOWO"
                user.avatar = "https://avatars.githubusercontent.com/u/1437467?v=4"
                user.gender = 1
                val jsonObject = HashMap<String, Any>()
                jsonObject["data"] = user
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else if (original.url.toString().contains("jowo-server/api/purchase/product")) {
                val jsonObject = HashMap<String, Any>()
                val data = ArrayList<HashMap<String, Any>>()
                data.add(HashMap<String, Any>().apply {
                    put("productId", "1")
                    put("coin", 1000)
                    put("price", "usd10")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 100)
                        put("label", "100Coin")
                    })
                })
                data.add(HashMap<String, Any>().apply {
                    put("productId", "2")
                    put("coin", 3000)
                    put("price", "usd30")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 300)
                        put("label", "300Coin")
                    })
                })
                data.add(HashMap<String, Any>().apply {
                    put("productId", "3")
                    put("coin", 6000)
                    put("price", "usd60")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 600)
                        put("label", "600Coin")
                    })
                })

                data.add(HashMap<String, Any>().apply {
                    put("productId", "4")
                    put("coin", 12000)
                    put("price", "usd120")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 1200)
                        put("label", "1200Coin")
                    })
                })

                data.add(HashMap<String, Any>().apply {
                    put("productId", "5")
                    put("coin", 18000)
                    put("price", "usd180")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 1800)
                        put("label", "1800Coin")
                    })
                })

                data.add(HashMap<String, Any>().apply {
                    put("productId", "6")
                    put("coin", 99900)
                    put("price", "usd999")
                    put("benefit", HashMap<String, Any>().apply {
                        put("coin", 9990)
                        put("label", "9990Coin")
                    })
                })
                jsonObject["data"] = data
                jsonObject["retCd"] = 0
                jsonObject["errorMessage"] = ""
                response = FakeResponse.fakeResponse(original, GsonUtils.toJson(jsonObject))
            } else {
                var request: okhttp3.Request = original.newBuilder()
                    .headers(header.build())
                    .method(original.method, original.body)
                    .build()
                request = encrypt(request)!!
                response = chain.proceed(request)
                response = decrypt(response)
            }
        } catch (e: Throwable) {
            //TODO 没有网络权限申请权限
            e.printStackTrace()
            LogUtils.iTag(TAG, "request url " + original.url)
            LogUtils.dTag(TAG, "intercept Throwable " + GsonUtils.toJson(e.stackTrace))

            if (e is IOException) {
                throw e
            } else {
                throw IOException(e)
            }
        }

        return response!!
    }

    private val header: Headers.Builder
        get() {
            val headers = Headers.Builder()
            if (!StringUtils.isEmpty(Current.getToken())) {
                headers.add(KEY_AUTHORIZATION, "Bearer " + Current.getToken())
            }

            val appId = NativeManager.get().getEncryptByKey(NativeManager.APP_ID)
            if (!StringUtils.isEmpty(appId)) {
                headers.add(KEY_APP_ID, appId)
            }
            val baseHeaderStr = BaseHeader.get().encryptStr
            headers.add(KEY_REQUEST, baseHeaderStr)

            return headers
        }

    @Throws(IOException::class)
    private fun encrypt(request: okhttp3.Request): Request {
        val requestBody = request.body

        if (requestBody == null) {
            LogUtils.iTag(TAG, request.toString())
            return request
        }
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        var charset = Charset.forName("UTF-8")
        val contentType = requestBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset)
        }

        val string = buffer.readString(charset)
        //模拟加密的方法，这里调用大家自己的加密方法就可以了
        val encryptStr = encryptStr(string) ?: ""
        val body = RequestBody.create(contentType, encryptStr)
        val requestNew = request.newBuilder()
            .headers(header.build())
            .method(request.method, body)
            .build()
        LogUtils.iTag(TAG, "request url " + requestNew.url)
        LogUtils.iTag(TAG, "request content $string")
        LogUtils.iTag(TAG, "request encryptStr $encryptStr")
        return requestNew
    }

    @Throws(IOException::class)
    private fun decrypt(response: Response?): Response? {
        var response = response ?: return null

        if (!response.isSuccessful) {
            LogUtils.dTag(TAG, response.toString())
            return response
        }

        //the response data
        val body = response.body
        val source = body!!.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer()
        var charset = Charset.defaultCharset()
        val contentType = body.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        val respString = buffer.clone().readString(charset)

        var bodyString: String? = null
        try {
            bodyString = decryptStr(respString)
            //解密方法，需要自己去实现
            val responseBody = ResponseBody.create(contentType, bodyString!!)
            response = response.newBuilder().body(responseBody).build()
            LogUtils.iTag(TAG, "resp content " + response.request.url + bodyString)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    private fun encryptStr(str: String): String? {
        var encryptStr: String? = null
        try {
            val aesKey = NativeManager.get().getEncryptByKey(NativeManager.AES_KEY)
            val aesIv = NativeManager.get().getEncryptByKey(NativeManager.AES_IV)
            encryptStr = AesUtils.encrypt(str, aesKey, aesIv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encryptStr
    }

    private fun decryptStr(encryptStr: String): String? {
        var str: String? = null
        try {
            // TODO 2、解密失败不捕获异常，返回请求失败
            val aesKey = NativeManager.get().getEncryptByKey(NativeManager.AES_KEY)
            val aesIv = NativeManager.get().getEncryptByKey(NativeManager.AES_IV)
            str = AesUtils.decrypt(encryptStr, aesKey, aesIv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

    companion object {
        private const val TAG = Constants.COMMON_TGA + "Network"
        const val KEY_AUTHORIZATION: String = "Authorization"
        const val KEY_APP_ID: String = "appId"
        const val KEY_REQUEST: String = "commonHeader"
    }
}
