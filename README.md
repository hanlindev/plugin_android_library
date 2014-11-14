plugin_android_library
======================

Plugin android library for Sana Mobile Client

What's in here
--------------
The libary code is in the PluginAndroidLibrary folder.
The other folders in the repository root are the sample apps showing the usage of the library and how to import the library. The ReactionDiagnosis requires additional hardware. Please refer to the readme in that project directory.

Prerequisite
------------
* Android Studio
* JDK 7
* Android SDK 19
* Android Build Tools 19.1

How to import the library
-------------------------
This section assumes that the Android app's project directory and the Library's project directory are in the same parent directory.

1. In the app's settings.gradle file, add the following lines:
```gradle
include ':PluginAndroidLibrary:plugin_android_library'
project (':PluginAndroidLibrary:plugin_android_library').projectDir = new File(settingsDir, '../PluginAndroidLibrary/plugin_android_library')
```
2. In app folder, build.gradle
Add to the dependencies
```gradle
compile project(path: ':PluginAndroidLibrary:plugin_android_library')
```
If you encounter compilation errors, try the following
Add
```gradle
android {
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/dependencies.txt'
        exclude 'META-INF/LGPL2.1'
    }
}
```
This should solve the compilation error caused by importing the library most of the times.

API Docs
--------
For a full set of API documentations, visit [here](http://didihl.github.io/plugin_android_library/apidocs).
The javadocs are in the gh-pages branch.
