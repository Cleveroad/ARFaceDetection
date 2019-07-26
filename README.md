# ARFaceDetection [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="19"></a>
![Header image](/images/header.png)
## Meet ARFaceDetection by Cleveroad
You know, guys, we have become a bit obsessed with music recently. See yourself by checking our previous work on the Audio Visualization View library. Since we wanted to create a companion to our equalizer and add to the collection of Android widgets, the idea to develop a nice and convenient audio widget was born almost immediately. So if you have already developed a music player and you feel that you want to make it even better, then you’re welcome to use our library and enjoy the results. 
![Demo image](/images/demo_.gif)
Our invention can facilitate the user’s interaction with a music player and nicely decorate his device screen. Moreover, it’s really easy to integrate! Read our <strong><a href="https://www.cleveroad.com/blog/case-study-audio-widget-overlay-view-by-cleveroad">Case Study: Audio Widget Overlay View by Cleveroad</a></strong> to ensure that and find more of useful information.
[![Awesome](/images/logo-footer.png)](https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts)
<br/>
## Setup
To use ARFaceDetection first add dependency to your project:
```groovy
dependencies {
    implementation 'com.google.firebase:firebase-core:16.0.9'
    implementation 'com.cleveroad.ARFaceDetection:ar-face-detection:1.0.2'
}
```
AndroidManifest
```xml
<manifest ...>
    ...
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-feature android:name="android.hardware.camera.ar" android:required="true"/>
    <uses-feature android:name="android.hardware.camera"/>
    <uses-feature android:name="android.hardware.camera.autofocus"/>
    <application ...>
    ....
        <meta-data 
            android:name="com.google.ar.core" 
            android:value="required"/>
        <meta-data
            android:name="com.google.firebase.ml.vision.DEPENDENCIES"
            android:value="face"/>
    ...
   </application>
</manifest>
```
Firebase
*   Create a Firebase project to connect to your Android app and setup it.
    Link to firebase console https://console.firebase.google.com/ 
*   Move your config file(google-services.json) into the module (app-level) directory of your app.
Application class
```groovy
class MyApp : Application() {
    ...
    override fun onCreate() {
        super.onCreate()
        ...
        FirebaseApp.initializeApp(this)
        ...   
   }
}
``` 
## Usage
Face detection val MLKit
```groovy
val fragment = FaceDetectorFragment.newInstance(R.drawable.icon)       
supportFragmentManager.beginTransaction().apply {
    replace(R.id.container, fragment, fragment.javaClass.simpleName)
}.commit()
```
Face detection val ARCore
```groovy
val fragment = AugmentedFacesFragment.newInstance(R.drawable.icon)
supportFragmentManager.beginTransaction().apply {
   replace(R.id.container, fragment, fragment.javaClass.simpleName)
}.commit()
```
## Restrictions
ARCore
minSdkVersion ≥ 24
supported devices: https://developers.google.com/ar/discover/supported-devices 
MLKIt 
minSdkVersion ≥ 21
## Changelog
See [changelog history].
<br />
#### Support ####
* * *
If you have any other questions regarding the use of this library, please contact us for support at info@cleveroad.com (email subject: "ARFaceDetection. Support request.") 
<br />
#### License ####
* * *
    The MIT License (MIT)
    
    Copyright (c) 2019 Cleveroad Inc.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
[changelog history]: /CHANGELOG.md
