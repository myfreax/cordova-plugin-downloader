// Type definitions for cordova-plugin-statusbar
// Project: https://github.com/apache/cordova-plugin-statusbar
// Definitions by: Xinkai Chen <https://github.com/Xinkai>
//                 Tim Brust <https://github.com/timbru31>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped

/**
 * Global object StatusBar.
 */
interface Window {
  Downloder: Downloder;
  addEventListener(
    type: "onProgress",
    listener: (ev: Event) => any,
    useCapture?: boolean
  ): void;
}

interface Download {
  id: number;
}

/**
 * The StatusBar object provides some functions to customize the iOS and Android StatusBar.
 */
interface Downloder {
  //pause a download
  pause(id: number, success: (id: number) => void, err: (msg: string) => void);
  //resume a download
  resume(id: number, success: (id: number) => void, err: (msg: string) => void);
  download(
    url: string,
    file: string,
    success: (id: number) => void,
    err: (msg: string) => void
  );
  //Query all downloads
  getDownloads(success: (id: Download[]) => void, err: (msg: string) => void);
  //Get all downloads with a status
  getDownloadsWithStatus(
    status: string,
    success: (id: Download[], err: (msg: string) => void) => void
  );
}

declare var Downloder: Downloder;
