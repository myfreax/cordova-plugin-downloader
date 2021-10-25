# cordova-wiregurad-plugin

## Prerequisites
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.INTERNET"/>
```
## install
```bash
cordova plugin add git@github.com:Bumoyu-com/cordova-plugin-downloader.git
```
## Configrution
Add following lines to your `config.xml` of cordova project 

```xml
<preference name="GradlePluginKotlinEnabled" value="true" />
<preference name="GradlePluginKotlinCodeStyle" value="official" />
<preference name="GradlePluginKotlinVersion" value="1.3.50" />
<preference name="AndroidXEnabled" value="true" />
```

## usage

```js
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);
    document.getElementById('deviceready').classList.add('ready');
    var id = -1
    document.getElementById("download").onclick = function () {
        Downloader.download(
            "https://speed.hetzner.de/100MB.bin",
            "/sdcard/Download/100MB1.txt",
            id => {
                alert(id)
                id = id
            },
            err => alert(err))
    }

    Downloader.pause(id)

    Downloader.resume(id)
    document.addEventListener('progress', (download) => {
        console.info(download.id)
        console.info(download.pregress)
        console.info(download.downloadedBytesPerSecond)
    }, false);
    
}
```
