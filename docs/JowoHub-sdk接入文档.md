# JowoHub SDK 短剧SDK接入文档

<!-- /TOC -->
- [短剧sdk接入](#短剧sdk接入)
  - [1 工程配置](#1-工程配置)
    - [1.1 添加依赖](#11-添加依赖)
    - [1.2 初始化](#12-初始化)
    - [1.3 混淆配置](#13-混淆配置)
  - [2 API接口说明](#2-api接口说明)
    - [2.1 请求banner列表](#21-请求banner列表)
    - [2.2 获取本地缓存banner](#22-获取本地缓存banner)
    - [2.3 请求block列表](#23-请求block列表)
    - [2.4 获取本地缓存block列表](#24-获取本地缓存block列表)
    - [2.5 请求label列表](#25-请求label列表)
    - [2.6 获取本地缓存列表](#26-获取本地缓存列表)
    - [2.7 请求支持的语言列表](#27-请求支持的语言列表)
    - [2.8 请求短剧详情](#28-请求短剧详情)
    - [2.9 本地缓存短剧详情](#29-本地缓存短剧详情)
    - [2.10 分页请求短剧](#210-分页请求短剧)
    - [2.11 本地缓存短剧列表](#211-本地缓存短剧列表)
    - [2.12 根据labelKey分页请求短剧](#212-根据labelkey分页请求短剧)
    - [2.13 根据label key获取本地缓存短剧](#213-根据label-key获取本地缓存短剧)
    - [2.14 根据block key 分页请求短剧](#214-根据block-key-分页请求短剧)
    - [2.15 根据block key获取本地缓存短剧](#215-根据block-key获取本地缓存短剧)
    - [2.16 请求短剧剧集](#216-请求短剧剧集)
    - [2.17 获取本地缓存的短剧集数数据](#217-获取本地缓存的短剧集数数据)
    - [2.18 请求短剧指定集数数据](#218-请求短剧指定集数数据)
    - [2.19 请求所有block以及对应短剧列表](#219-请求所有block以及对应短剧列表)
    - [2.20 本地缓存的所有块以及对应的短剧列表](#220-本地缓存的所有块以及对应的短剧列表)
    - [2.21 上报播放历史](#221-上报播放历史)
    - [2.22 收藏短剧](#222-收藏短剧)
    - [2.23 分页请求收藏短剧列表](#223-分页请求收藏短剧列表)
    - [2.24 取消短剧收藏](#224-取消短剧收藏)
    - [2.25 请求短剧当前用户解锁集数](#225-请求短剧当前用户解锁集数)
    - [2.26 获取缓存短剧当前用户解锁集数](#226-获取缓存短剧当前用户解锁集数)
    - [2.27 分页请求短剧播放历史](#227-分页请求短剧播放历史)
    - [2.28 删除短剧播放历史](#228-删除短剧播放历史)
    - [2.29 获取本地缓存短剧播放历史](#229-获取本地缓存短剧播放历史)
    - [2.30 获取指定短剧缓存的播放历史](#230-获取指定短剧缓存的播放历史)
    - [2.31 请求for u 列表](#231-请求for-u-列表)
    - [2.32 缓存的最后一次for u列表](#232-缓存的最后一次for-u列表)
    - [2.33 解锁单集接口](#233-解锁单集接口)
    - [2.34 批量解锁接口](#234-批量解锁接口)
    - [2.35 批量请求短剧详情](#235-批量请求短剧详情)
    - [2.36 获取剧集的分享链接](#236-获取剧集的分享链接)
    - [2.37 举报剧集](#237-举报剧集)
    - [2.38 搜索短剧](#238-搜索短剧)
    
  - [3 播放接口](#3-播放接口)
    - [3.1 获取播放DramaPlayerUiFragment](#31-获取播放dramaplayeruifragment)
    - [3.2 获取播放单剧DramaEpisodesPlayerUiFragment](#32-获取播放单剧dramaepisodesplayeruifragment)
    - [3.3 PlayerUiStyle 属性说明](#33-playeruistyle-属性说明)

## 1 工程配置

### 1.1 添加依赖
- 将需要sdl的aar复制到项目libs目录
- 项目级build.gradle中添加
```java
repositories {
    flatDir {
        dirs 'libs'
    }
}
```
- 在app/build.gradle中添加
```java

implementation(name: 'jowosdk-xxx', ext: 'aar')
// jowo sdk  start
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-runtime:2.6.1")
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("org.bouncycastle:bcprov-jdk15on:1.67")

implementation("androidx.viewpager2:viewpager2:1.1.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.media3:media3-exoplayer:1.3.1")
implementation("androidx.media3:media3-exoplayer-hls:1.3.1")
implementation("androidx.media3:media3-ui:1.3.1")
//jowo sdk  end
    
```

### 1.2 初始化
- 建议在Application中初始化，所有接口必须在初始化话成功后才能调用
- 中国大陆无法访问，请在海外网络环境下调用sdk
```java
JOWOSdk.Config config = new JOWOSdk.Config.Builder()
    //分配的appid
    .setAppId("") 
    //分配的aes key
    .setAesKey("")
    // 分配的aes iv
    .setAesIv("")
    // 开启debug，过滤tag:jowo，上线请关闭
    .setDebug(true)
    // 接入者用户唯一标识，会绑定到jowo用户系统
    // 接入者用户后续登录可通过JOWOSdk.setUserId设置，需重新请求数据
    .setUserId("")
    // 安卓ID
    .setAndroidId("")
    // 短剧数据的语言，, 默认使用Locale.getDefault().toLanguageTag()
    //只支持设置jowo后台支持的语言
    //初始化后支持使用JOWOSdk.setLanguage更新，需重新请求数据
    .setLanguage("")
    .build();
JOWOSdk.init(this, config, result -> {
    if(result != null && result.getCode() == Result.Code.OK){
        // 初始化成功后再调用其他短剧相关接口
    }
});
```

### 1.3 混淆配置
```java
#okhttp3.x
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.internal.platform.*

#retrofit2.x
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keep class c.plus.plan.jowosdk.entity.** { *; }
```

## 2 API接口说明
> **dramaId即为Drama中的jowoVid**

### 2.1 请求banner列表
```java
JOWOSdk.fetchBanner(Callback<List<Banner>> callback)
```
### 2.2 获取本地缓存banner
```java
JOWOSdk.lastBanner(Callback<List<Banner>> callback)
```
### 2.3 请求block列表
```java
JOWOSdk.fetchBlocks(Callback<List<Block>> callback)
```
### 2.4 获取本地缓存block列表
```java
JOWOSdk.lastBlocks(Callback<List<Block>> callback)
```
### 2.5 请求label列表
```java
JOWOSdk.fetchLabels(Callback<List<Label>> callback)
```
### 2.6 获取本地缓存列表
```java
JOWOSdk.lastLabels(Callback<List<Label>> callback)
```
### 2.7 请求支持的语言列表
```java
JOWOSdk.fetchLanguages(Callback<List<String>> callback)
```
### 2.8 请求短剧详情
```java
//id 短剧id
JOWOSdk.fetchDrama(String dramaId, Callback<Drama> callback)
```
### 2.9 本地缓存短剧详情
```java
//id 短剧id
JOWOSdk.lastDramaById(String dramaId, Callback<Drama> callback)
```
### 2.10 分页请求短剧
```java
//cursorId 分页下表，第一页传0，后续传入返回参数PageResult.cursorId
JOWOSdk.fetchDramasPerPage(int cursorId, Callback<PageResult<List<Drama>>> callback)
```
### 2.11 本地缓存短剧列表
- 只返回第一页
```java
JOWOSdk.lastDramas(Callback<List<Drama>> callback)
```
### 2.12 根据labelKey分页请求短剧
```java
//cursorId 分页下表，第一页传0，后续传入返回参数PageResult.cursorId
//labelKey Label类中对应的字段
JOWOSdk.fetchDramasByLabelKeyPerPage(String labelKey, int cursorId, 
    Callback<PageResult<List<Drama>>> callback)
```
### 2.13 根据label key获取本地缓存短剧
- 只缓存一页
```java
//labelKey Label类中对应的字段
JOWOSdk.lastDramasByLabelKey(String labelKey, Callback<List<Drama>> callback)
```
### 2.14 根据block key 分页请求短剧
```java
//blockKey Block类中对应的字段
JOWOSdk.fetchDramasByBlockKeyPerPage(String blockKey, int cursorId, 
    Callback<PageResult<List<Drama>>> callback)
```
### 2.15 根据block key获取本地缓存短剧
- 只缓存一页
```java
//labelKey Block类中对应的字段
JOWOSdk.fetchDramasByBlockKeyPerPage(String blockKey, int cursorId, 
    Callback<PageResult<List<Drama>>> callback)
```
### 2.16 请求短剧剧集
- 返回剧集num大于greaterEpisodesNum的剧，最多10集
```java
//dramaId 短剧ID
//开始集数greaterEpisodesNum(不包含当前集)
JOWOSdk.fetchEpisodesGreaterNum(String dramaId, int greaterEpisodesNum,
     Callback<List<Episodes>> callback)
```
### 2.17 获取本地缓存的短剧集数数据
```java
//dramaId 短剧ID
JOWOSdk.lastEpisodesByDramaId(String dramaId, Callback<List<Episodes>> callback)
```
### 2.18 请求短剧指定集数数据
```java
//dramaId 短剧ID
//episodesNum 指定集数
JOWOSdk.fetchEpisodesByNum(String dramaId, int episodesNum, Callback<Episodes> callback)
```
### 2.19 请求所有block以及对应短剧列表
- (列表只返回一页)
```java
//dramaId 短剧ID
//episodesNum 指定集数
JOWOSdk.fetchBlockDrama(Callback<List<BlockAndDramas>> callback)
```
### 2.20 本地缓存的所有块以及对应的短剧列表
```java
//dramaId 短剧ID
//episodesNum 指定集数
JOWOSdk.lastBlockDrama(Callback<List<BlockAndDramas>> callback)
```
### 2.21 上报播放历史
```java
//* @param dramaId 短剧id
//* @param episodeNum 短剧第几集
//* @param progressTimeMillis 播放进度
JOWOSdk.postDramaHistory(String dramaId, int episodeNum, long progressTimeMillis,
     Callback callback)
```
### 2.22 收藏短剧
```java
//* @param dramaId 短剧id
//* @param episodeNum 短剧第几集
//* @param language 短剧语言
JOWOSdk.postDramaCollect(String dramaId, String language, Callback callback)
```
### 2.23 分页请求收藏短剧列表
```java
//cursorId 分页下表，第一页传0，后续传入返回参数PageResult.cursorId
JOWOSdk.fetchUserDramaCollectsPerPage(int cursorId, 
    Callback<PageResult<List<Drama>>> callback)
```
### 2.24 取消短剧收藏
```java
//* @param dramaId 短剧id
JOWOSdk.deleteDramaCollectCancel(String dramaId, Callback callback)
```
### 2.25 请求短剧当前用户解锁集数
```java
//* @param dramaId 短剧id
JOWOSdk.fetchUserDrama(String dramaId, Callback<UserUnlockEpisodes> callback)
```
### 2.26 获取缓存短剧当前用户解锁集数
```java
//* @param dramaId 短剧id
JOWOSdk.lastUserDrama(String dramaId, Callback<UserUnlockEpisodes> callback)
```
### 2.27 分页请求短剧播放历史
```java
/**
 * @param cursorId 分页下表，第一页传0，后续传入返回参数PageResult.cursorId
 */ 
JOWOSdk.fetchUserDramaHistoriesPerPage(int cursorId, 
    Callback<PageResult<List<DramaHistory>>> callback)
```
### 2.28 删除短剧播放历史
```java
//dramaId 短剧id
JOWOSdk.deleteDramaHistory(String dramaId,  Callback callback)
```
### 2.29 获取本地缓存短剧播放历史
- 最多返回20条
```java
JOWOSdk.lastHistories(Callback<List<DramaHistory>> callback){
        SDKManager.getInstance().lastHistories(callback)
```
### 2.30 获取指定短剧缓存的播放历史
```java
JOWOSdk.lastHistoryByDramaId(String dramaId, Callback<DramaHistory> callback)
```
### 2.31 请求for u 列表
- 每次请求会根据推荐返回不一样的随机列表（可能重复）
```java
JOWOSdk.fetchDramasForU(Callback<List<DramaForU>> callback)
```
### 2.32 缓存的最后一次for u列表
```java
JOWOSdk.lastDramasForU(Callback<List<DramaForU>> callback)
```
### 2.33 解锁单集接口
```java
/**
     * 解锁短剧，解锁只做业务功能
     * @param dramaId 短剧id
     * @param episodeNum 短剧第几集
     */
JOWOSdk.unlockEpisodes(String dramaId, int episodeNum, Callback callback)
```
### 2.34 批量解锁接口
```java
/**
     * 批量解锁短剧，解锁只做业务功能
     * @param dramaId 短剧id
     * @param episodeNumFrom 解锁开始集
     * @param episodeNumTo 解锁结束集
     */
JOWOSdk.unlockEpisodesList(String dramaId, int episodeNumFrom, int episodeNumTo, Callback callback)
```
### 2.35 批量请求短剧详情
```java
 /**
     * 请求短剧详情
     * @param ids 短剧集合
     * @param callback
     */
JOWOSdk.fetchDramaList(int[] ids, Callback<List<Drama>> callback)
```
### 2.36 获取剧集的分享链接
```java
 /**
     * 获取剧集的分享链接
     * @param dramaId 短剧id
     * @param episodeNum 短剧第几集
     * @param callback
     */
JOWOSdk.fetchEpisodeShareUrl(String dramaId, int episodeNum, Callback<EpisodeShareUrl> callback)
```

### 2.37 举报剧集
```java
 /**
     * 举报剧集
     * @param feedback 举报信息
     * @param callback
     */
JOWOSdk.postFeedback(Feedback feedback, Callback callback)
```

### 2.38 搜索短剧
```java
 /**
     * 举报剧集
     * @param cursorId 分页下表，第一页传0，后续传入返回参数PageResult.cursorId
     * @param keywords 搜索关键字
     * @param callback
     */
JOWOSdk.searchDramas(int cursorId, String[] keywords, Callback<PageResult<List<Drama>>> callback)
```

## 3 播放接口
### 3.1 获取播放DramaPlayerUiFragment
- 用于实现foru等短剧列表直接播放的页面
- 参考TabForUFragment
```java
/**
     * 获取单集播放fragment
     * @param dramaId
     * @param episodesNum
     * @param playerPosition
     * @param checkLock 是否检查当集已解锁。用于vip订阅时不许解锁直接点播播放，false时不会回调OnShowListener.showUnlock直接播放
     * @param historyEnable 是否记录播放历史，默认trye
     * @param playerUiStyle UI风格
     * @return
     */
JOWOSdk.getDramaPlayerFragment(String dramaId, int episodesNum, long playerPosition, 
boolean checkLock, boolean historyEnable, PlayerUiStyle playerUiStyle) 
```
- fragment设置事件监听回调
```
fragment.setOnShowListener(onShowListener);
fragment.setOnPlayerListener(onPlayerListener);

/**
 * ExoPlayer播放器事件监听
 */
fragment.setAndroidPlayerListener(onAnddroidPlayerListener);
```
### 3.2 获取播放单剧DramaEpisodesPlayerUiFragment
- 用于实现单个短剧多级放的页面
- 参考PlayerActivity
```java
/**
     * 获取剧所有集播放fragment
     * @param dramaId 短剧id
     * @param episodesNum 短剧剧集
     * @param playerPosition 播放进度
     * @param checkLock 是否检查当集已解锁。用于vip订阅时不许解锁直接点播播放，false时不会回调OnShowListener.showUnlock直接播放
     * @param historyEnable 是否记录播放历史，默认trye
     * @param playerUiStyle UI风格
     * @return
     */
JOWOSdk.getDramaEpisodesPlayerFragment(String dramaId, int episodesNum, String cover, long playerPosition, 
boolean checkLock, boolean historyEnable, PlayerUiStyle playerUiStyle)  
```
- fragment设置事件监听回调
```
fragment.setOnShowListener(onShowListener);
fragment.setOnPlayerListener(onPlayerListener);


/**
 * ExoPlayer播放器事件监听
 */
fragment.setAndroidPlayerListener(onAnddroidPlayerListener);
``` 

### 3.3 PlayerUiStyle 属性说明
- setUiType 设置UI类型
    ```
    public enum PlayerUiType {
        NORMAL, // 常规
        FOR_YOU // For You
    }
    ```
- setShowCollect 是否显示收藏
- setCollectSrc 收藏图标
- setCollectSelectedSrc 已收藏图标
- setCollect 收藏文案
- setCollectSelected 已收藏文案
- setCollectColor 收藏文案颜色
- setCollectSize 收藏文案字体大小
- setShowEpisodes 显示剧集列表入口
- setEpisodesSrc 剧集列表图标
- setEpisodes 剧集列表文案
- setEpisodesColor 剧集列表文案颜色
- setEpisodesSize 剧集列表文案字体大小
- setShowDetail 是否显示跳转到详情（仅For You）
- setDetailSrc 跳转到详情图标（仅For You）
- setDetail 跳转到详情文案（仅For You）
- setDetailColor 跳转到详情文案颜色（仅For You）
- setDetailSize 跳转到详情文案字体大小（仅For You）
- setShowBack 是否显示返回
- setBackRes 返回图标
- setTitleColor 标题颜色
- setTitleSize 标题大小
- setTopEpisodesNumSize 标题当前集数大小
- setTopEpisodesNumColor 标题当前集数颜色
- setTopEpisodesCountSize 总集数大小
- setTopEpisodesCountColor 总集数颜色
- setShowMenu 是否显示菜单
- setMenuSrc 菜单图标
- setTopBackgroundColor 顶部标题背景色
- setBackgroundColor 背景色
- setBottomBackgroundColor 底部进度条区域背景色
- setPlayedColor 进度条已播放颜色
- setUnPlayedColor 未播放进度条颜色
- setBufferedColor 进度条已缓冲颜色
- setScrubberColor 进度条颜色
- setTimePositionColor 播放进度颜色
- setTimeDurationColor 播放总时长颜色
- setHeaderPaddingTop 顶部padding 用来fragment全屏时设置
- ~~setSubtitleSize 外挂字幕文字大小 (已弃用)~~
- ~~setSubtitleColor 外挂字幕文字颜色 (已弃用)~~
- ~~setSubtitleBackgroundColor 外挂字幕文字背景色 (已弃用)~~
- setLoadingRes 自定义加载view
- setPlayIconRes 播放图标

**务必注意：为了保护版权方的合法权益，播放界面的标题部分严禁隐藏或修改。此区域包含重要的版权信息，任何形式的篡改都将构成侵权行为。**
