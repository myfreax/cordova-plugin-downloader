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


    
    document.getElementById("Start").onclick = function () {
        //6秒内执行6次数据采集
        Downloader.startTimeoutCheck(6000, 6, () => { console.info("started"), (err) => { console.info(err) } });
    }
    document.getElementById("Stop").onclick = function () {
        Downloader.stopTimeoutCheck((e) => console.info("service stop success"), (err) => { console.info("service stop fail") })
    }

    document.getElementById("GetTimeoutTask").onclick = function () {
        Downloader.getTimeoutTasks((e) => console.info(e), (err) => { console.info(err) })
    }

    
    document.addEventListener('onProgress', (download) => {
        console.info(download.id)
        console.info(download.pregress)
        console.info(download.downloadedBytesPerSecond)
    }, false);

    document.addEventListener('onAdded', function (e) {
        console.info(e)
        console.info('Event of type: onAdded')
    }, false);

    document.addEventListener('onCancelled', function (e) {
        console.info(e)
        console.info('Event of type: onCancelled')
    }, false);


    document.addEventListener('onCompleted', function (e) {
        console.info(e)
        console.info('Event of type: onCompleted')
    }, false);

    document.addEventListener('onDeleted', function (e) {
        console.info(e)
        console.info('Event of type: onDeleted')
    }, false);

    document.addEventListener('onDownloadBlockUpdated', function (e) {
        console.info(e)
        console.info('Event of type: onDownloadBlockUpdated')
    }, false);

    document.addEventListener('onError', function (e) {
        console.info(e)
        console.info('Event of type: onError')
    }, false);

    document.addEventListener('onPaused', function (e) {
        console.info(e)
        console.info('Event of type: onPaused')
    }, false);


    document.addEventListener('onProgress', function (e) {
        console.info(e)
        console.info('Event of type: onProgress')
    }, false);


    document.addEventListener('onQueued', function (e) {
        console.info(e)
        console.info('Event of type: onQueued')
    }, false);

    document.addEventListener('onRemoved', function (e) {
        console.info(e)
        console.info('Event of type: onRemoved')
    }, false);


    document.addEventListener('onResumed', function (e) {
        console.info(e)
        console.info('Event of type: onResumed')
    }, false);


    document.addEventListener('onStarted', function (e) {
        console.info(e)
        console.info('Event of type: onStarted')
    }, false);


    document.addEventListener('onWaitingNetwork', function (e) {
        console.info(e)
        console.info('Event of type: onWaitingNetwork')
    }, false);
    
}
```
