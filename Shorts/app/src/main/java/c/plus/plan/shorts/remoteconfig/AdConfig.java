package c.plus.plan.shorts.remoteconfig;

import com.google.gson.annotations.SerializedName;

public class AdConfig {
    public final static String CONFIG_KEY = "ad_config";
    private static final int DEFAULT_HOME_TAB_AD_FIRST = 1;
    private static final int DEFAULT_HOME_TAB_AD_INTERVAL = 3;
    private static final int DEFAULT_UNLOCK_EPISODES_AD_COUNT = 2;
    private static final int DEFAULT_FOR_YOU_SLIDE_AD_COUNT = 3;
    private static final int DEFAULT_HOME_SLIDE_AD_COUNT = 3;

    private static AdConfig adConfig;

    @SerializedName("home_tab_list_ad")
    private HomeTabListAd homeTabListAd;
    @SerializedName("unlock_episodes")
    private UnlockEpisodes unlockEpisodes;
    @SerializedName("foryou_slide")
    private ForYouSlide forYouSlide;
    @SerializedName("home_slide")
    private HomeSlide homeSlide;

    public static synchronized AdConfig get(){

        if(adConfig == null){
            adConfig = new AdConfig();
        }

        return adConfig;
    }

    public HomeTabListAd getHomeTabListAd() {
        if(homeTabListAd == null){
            homeTabListAd = new HomeTabListAd();
            homeTabListAd.setFirst(DEFAULT_HOME_TAB_AD_FIRST);
            homeTabListAd.setInterval(DEFAULT_HOME_TAB_AD_INTERVAL);
        }
        return homeTabListAd;
    }

    public void setHomeTabListAd(HomeTabListAd homeTabListAd) {
        this.homeTabListAd = homeTabListAd;
    }

    public UnlockEpisodes getUnlockEpisodes() {
        if(unlockEpisodes == null){
            unlockEpisodes = new UnlockEpisodes();
            unlockEpisodes.setAdCount(DEFAULT_UNLOCK_EPISODES_AD_COUNT);
        }
        return unlockEpisodes;
    }

    public void setUnlockEpisodes(UnlockEpisodes unlockEpisodes) {
        this.unlockEpisodes = unlockEpisodes;
    }

    public ForYouSlide getForYouSlide() {
        if(forYouSlide == null){
            forYouSlide = new ForYouSlide();
        }
        return forYouSlide;
    }

    public void setForYouSlide(ForYouSlide forYouSlide) {
        this.forYouSlide = forYouSlide;
    }

    public HomeSlide getHomeSlide() {
        if(homeSlide == null){
            homeSlide = new HomeSlide();
        }
        return homeSlide;
    }

    public void setHomeSlide(HomeSlide homeSlide) {
        this.homeSlide = homeSlide;
    }

    public static class UnlockEpisodes {
        @SerializedName("ad_count")
        private int adCount;

        public int getAdCount() {
            if(adCount <= 0){
                return DEFAULT_UNLOCK_EPISODES_AD_COUNT;
            }
            return adCount;
        }

        public void setAdCount(int adCount) {
            this.adCount = adCount;
        }
    }

    public static class ForYouSlide {
        @SerializedName("ad_count")
        private int adCount;

        public int getAdCount() {
            if(adCount <= 0){
                return DEFAULT_FOR_YOU_SLIDE_AD_COUNT;
            }
            return adCount;
        }

        public void setAdCount(int adCount) {
            this.adCount = adCount;
        }
    }

    public static class HomeSlide {
        @SerializedName("ad_count")
        private int adCount;

        public int getAdCount() {
            if(adCount <= 0){
                return DEFAULT_HOME_SLIDE_AD_COUNT;
            }
            return adCount;
        }

        public void setAdCount(int adCount) {
            this.adCount = adCount;
        }
    }


    public static class HomeTabListAd {
        @SerializedName("ad_first")
        private int first;
        @SerializedName("ad_other_insert_interval")
        private int interval;

        public int getFirst() {
            if(first <0 ){
                first = DEFAULT_HOME_TAB_AD_FIRST;
            }
            // 实际使用下标从0开始，配置1开始
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getInterval() {
            if(interval<=0){
                return DEFAULT_HOME_TAB_AD_INTERVAL;
            }
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }
    }
}
