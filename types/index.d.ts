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
  getDownloads(
    success: (downloads: Download[]) => void,
    err: (msg: string) => void
  );

  delete(
    ids: number[],
    success: (ids: number[]) => void,
    err: (msg: string) => void
  );

  remove(
    ids: number[],
    success: (ids: number[]) => void,
    err: (msg: string) => void
  );
  /**
   *
   * @param status
   * @param success
    0 -> NONE
    1 -> QUEUED
    2 -> DOWNLOADING
    3 -> PAUSED
    4 -> COMPLETED
    5 -> CANCELLED
    6 -> FAILED
    7 -> REMOVED
    8 -> DELETED
    9 -> ADDED
   */
  //Get all downloads with a status
  getDownloadsWithStatus(
    status: number,
    success: (downloads: Download[]) => void,
    err: (msg: string) => void
  );
}

declare var Downloder: Downloder;
