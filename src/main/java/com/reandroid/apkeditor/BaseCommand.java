/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.apkeditor;

import com.reandroid.apk.APKLogger;
import com.reandroid.archive.APKArchive;
import com.reandroid.apk.ApkModule;
import com.reandroid.commons.utils.log.Logger;

import java.io.IOException;
import java.util.regex.Pattern;

public class BaseCommand implements APKLogger {
    private String mLogTag;
    private boolean mEnableLog;
    public BaseCommand(){
        mLogTag = "";
        mEnableLog = true;
    }
    public void run() throws IOException{

    }

    protected void setLogTag(String tag) {
        if(tag == null){
            tag = "";
        }
        this.mLogTag = tag;
    }
    public void setEnableLog(boolean enableLog) {
        this.mEnableLog = enableLog;
    }
    @Override
    public void logMessage(String msg) {
        if(!mEnableLog){
            return;
        }
        Logger.i(mLogTag + msg);
    }
    @Override
    public void logError(String msg, Throwable tr) {
        if(!mEnableLog){
            return;
        }
        Logger.e(mLogTag + msg, tr);
    }
    @Override
    public void logVerbose(String msg) {
        if(!mEnableLog){
            return;
        }
        Logger.sameLine(mLogTag + msg);
    }
    public void logWarn(String msg) {
        Logger.e(mLogTag + msg);
    }

    protected static void clearMeta(ApkModule module){
        removeSignature(module);
        module.setApkSignatureBlock(null);
    }
    protected static void removeSignature(ApkModule module){
        APKArchive archive = module.getApkArchive();
        archive.removeAll(Pattern.compile("^META-INF/.+\\.(([MS]F)|(RSA))"));
        archive.remove("stamp-cert-sha256");
    }
}

