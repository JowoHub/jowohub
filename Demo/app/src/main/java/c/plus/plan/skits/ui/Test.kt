package c.plus.plan.skits.ui

import android.content.Context
import android.view.View
import c.plus.plan.jowosdk.entity.Drama
import c.plus.plan.jowosdk.entity.Episodes
import c.plus.plan.jowosdk.sdk.JOWOSdk
import c.plus.plan.jowosdk.sdk.Listener

/**
 * @author Joey.zhao
 * @date 2025/4/23 17:24
 * @description
 */
class Test {

    fun test() {
        val fragment = JOWOSdk.getDramaPlayerFragment(
            "1",
            1,
            0,
            true,
            false,
            null
        )
        fragment.setOnShowListener(object : Listener.OnShowListener  {
            override fun showEpisodesList(context: Context?, drama: Drama?, currentNum: Int) {
                TODO("Not yet implemented")
            }

            override fun showUnlock(context: Context?, drama: Drama?, episodes: Episodes?) {
                TODO("Not yet implemented")
            }

            override fun showUnLockResult(
                context: Context?,
                dramaId: Int,
                num: Int,
                success: Boolean,
            ) {
                TODO("Not yet implemented")
            }

            override fun showCollectResult(context: Context?, success: Boolean) {
                TODO("Not yet implemented")
            }

            override fun showCancelCollectResult(context: Context?, success: Boolean) {
                TODO("Not yet implemented")
            }

            override fun showMenu(
                context: Context?,
                view: View?,
                drama: Drama?,
                extras: HashMap<String, Any>?,
            ) {
                TODO("Not yet implemented")
            }

            override fun showLastPageUp() {
                TODO("Not yet implemented")
            }

        })

    }
}