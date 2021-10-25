/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

/* global cordova */
var cordova = require('cordova');
cordova.fireDocumentEvent('offline');

var exec = require('cordova/exec');
var channel = require('cordova/channel');
var utils = require('cordova/utils');


var Downloader = {
  //pause(id: number, success: (id: number) => void, err: (msg: string) => void);
  pause(id, success, err) {
    exec(success, err, 'Downloader', 'pause', [id]);
  },
  //resume(id: number, success: (id: number) => void, err: (msg: string) => void);
  resume(id, success, err) {
    exec(success, err, 'Downloader', 'resume', [id]);
  },
  ///Query all downloads
  // getDownloads(success: (id: Download[]) => void, err: (msg: string) => void);
  getDownloads(success, err) {
    exec(success, err, 'Downloader', 'getDownloads', []);
  },
  //Get all downloads with a status
  /**
   * getDownloadsWithStatus(
    status: string,
    success: (id: Download[], err: (msg: string) => void) => void
  );
   */
  getDownloadsWithStatus(status, success, err) {
    exec(success, err, 'Downloader', 'getDownloadsWithStatus', [status]);
  },
  /**
   * download(
    url: string,
    file: string,
    success: (id: number) => void,
    err: (msg: string) => void
  );
   * @param {*} url 
   * @param {*} file 
   */
  download(url, file, success, err) {
    exec(success, err, 'Downloader', 'download', [url, file]);
  },

  // getProgress(success, err) {
  //   exec(success, err, 'Downloader', 'getProgress', []);
  // }

};

cordova.fireDocumentEvent('progress');

module.exports = Downloader;