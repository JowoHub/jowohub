# JowoHub SDK Update Log

## v1.3.5
- Added callback to `OnPlayerListener`
    ```java
    public void controllerVisible(Drama drama, Episodes episodes, boolean visible) {
        LogUtils.dTag(TAG, "controllerVisible", drama.getJowoVid(), episodes.getEpisodesNum(), visible);
        // Callback for the visibility of the controller. You can control the visibility of bars based on whether the current episode is being played for the first time.
        //    if (mFragment != null) {
        //      View view = mFragment.requireView();
        //      view.findViewById()
        //    }
    }
    ```
- Added method to get the current playback position of the interface
    ```java
    public long getPlayerCurrentPosition();
    ```
- Added method to set the playback speed of the interface
    ```java
    public boolean setPlayerSpeed(float speed);
    ```
- Fixed known bugs