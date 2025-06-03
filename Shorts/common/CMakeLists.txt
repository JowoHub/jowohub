# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

set(EXECUTABLE_OUTPUT_PATH      "${CMAKE_CURRENT_SOURCE_DIR}/src/main/assets/${ANDROID_ABI}")
#add_executable(daemon
#
#                # Provides a relative path to your source file(s).
#                src/main/cpp/NativeUtils.cpp
#                src/main/cpp/Daemon.cpp)

# 去掉SO编译，在改变的时候本地编译
add_library( # Specifies the name of the library.
             common

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/NativeManager.cpp
             src/main/cpp/NativeUtils.cpp
             src/main/cpp/EncryptManager.cpp
             src/main/cpp/UrlManager.cpp
             src/main/cpp/md5.cpp)

#add_library( # Specifies the name of the library.
#             weather_daemon_api20
#
#             # Sets the library as a shared library.
#             SHARED
#
#             # Provides a relative path to your source file(s).
#             src/main/cpp/NativeUtils.cpp
#             src/main/cpp/DaemonApi20.cpp
#             src/main/cpp/md5.cpp)

#add_library( # Specifies the name of the library.
#             weather_daemon_api21
#
#             # Sets the library as a shared library.
#             SHARED
#
#             # Provides a relative path to your source file(s).
#             src/main/cpp/NativeUtils.cpp
#             src/main/cpp/DaemonApi21.cpp
#             src/main/cpp/md5.cpp)


#解决打印log报错问题
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log)

#target_link_libraries( # Specifies the target library.
#                   weather_daemon_api20
#                   # Links the target library to the log library
#                   # included in the NDK.
#                   ${log-lib})
#
#target_link_libraries( # Specifies the target library.
#                   weather_daemon_api21
#                   # Links the target library to the log library
#                   # included in the NDK.
#                   ${log-lib})
#
#target_link_libraries( # Specifies the target library.
#                   daemon
#                   # Links the target library to the log library
#                   # included in the NDK.
#                   ${log-lib})

include_directories(src/main/cpp/)