package c.plus.plan.shorts.sdk

import android.content.Context
import c.plus.plan.jowosdk.entity.Episodes
import c.plus.plan.jowosdk.sdk.Callback
import c.plus.plan.jowosdk.sdk.JOWOSdk
import c.plus.plan.jowosdk.sdk.Result
import c.plus.plan.shorts.R
import c.plus.plan.shorts.ui.activity.DramaEpisodesActivity
import c.plus.plan.shorts.ui.activity.PlayerActivity
import com.blankj.utilcode.util.ToastUtils

/**
 * @author Joey.zhao
 * @date 2025/3/22 13:18
 * @description
 */
object SDKExt {
    @JvmStatic
    fun playEpisodes(context: Context, dramaId: String, episodeNum: Int, language: String) {
        JOWOSdk.unlockEpisodes(dramaId, episodeNum, object : Callback<Episodes> {
            override fun result(result: Result<Episodes>?) {
                if (result?.data == null) {
                    ToastUtils.showShort(context.getString(c.plus.plan.jowosdk.R.string.jowo_play_episodes_fail))
                    return
                }
                PlayerActivity.start(context, dramaId, episodeNum, result.data.cover, 0)
            }

        });

    }

    @JvmStatic
    fun playDrama(
        context: Context,
        dramaId: String,
        cover: String,
        episodeNum: Int,
        progressTimeMillis: Long,
    ) {
        PlayerActivity.start(context, dramaId, episodeNum, cover, progressTimeMillis)
    }


}
