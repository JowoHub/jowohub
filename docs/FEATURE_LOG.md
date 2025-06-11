# JowoHub SDK 更新日志

Please refer to the English document here: [EN](docs/FEATURE_LOG_EN.md)

## v1.3.5
- OnPlayerListener增加回调
    ```java
    public void controllerVisible(Drama drama, Episodes episodes, boolean visible){
        LogUtils.dTag(TAG, "controllerVisible", drama.getJowoVid(), episodes.getEpisodesNum(), visible);
        // controller的显隐回调 可以根据当前播放剧集是不是第一次播放控制bars的显隐等逻辑
        //    if (mFragment != null) {
        //      View view = mFragment.requireView();
        //      view.findViewById()
        //    }
    }
  ```
- 可获取当前界面的播放进度
    ```java
    public long getPlayerCurrentPosition();
    ```
- 可设置当前界面的播放速度
    ```java
    public boolean setPlayerSpeed(float speed);
    ```
- 修复已知bug