# ARFaceDetection [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="19"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="19"></a>
![Header image](/images/header.png)
## Meet ARFaceDetection by Cleveroad
We’re glad to share with you our latest AR-based library for Android. It’s designed with ARCore technology and is capable of detecting faces and overlaying images above the user’s head considering multiple parameters to make them look realistic (e.g. head movements).

![Demo image](/images/demo_.gif)

You can try the functionality of the library right on your devices! Go to this link to download our application from <a target="_blank"  href="https://play.google.com/store/apps/details?id=com.cleveroad.aropensource">Google Play Store</a>.

[![Awesome](/images/google-play.png)](https://play.google.com/store/apps/details?id=com.cleveroad.aropensource)

[![Awesome](/images/logo-footer_.png)](https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts)
<br/>
## Setup
To use ARFaceDetection first add dependency to your project:
```groovy
dependencies {
    implementation 'com.google.firebase:firebase-core:17.4.3'
    implementation 'com.cleveroad.ARFaceDetection:ar-face-detection:1.0.4'
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
Face detection val MLKit using CameraX
```groovy
val fragment = FaceDetectorCameraXFragment.newInstance(R.drawable.icon)
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
