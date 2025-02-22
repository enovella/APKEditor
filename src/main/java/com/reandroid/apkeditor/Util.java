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

import com.reandroid.apk.ApkModule;
import com.reandroid.arsc.array.StringArray;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.item.IntegerItem;
import com.reandroid.arsc.item.TableString;
import com.reandroid.arsc.pool.TableStringPool;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Util {
    public static boolean isHelp(String[] args){
        if(isEmpty(args)){
            return true;
        }
        return isHelp(args[0]);
    }
    public static boolean containsHelp(String[] args){
        if(isEmpty(args)){
            return false;
        }
        for(String command : args){
            if(isHelp(command)){
                return true;
            }
        }
        return false;
    }
    public static boolean isHelp(String command){
        if(isEmpty(command)){
            return false;
        }
        command=command.toLowerCase().trim();
        return command.equals("-h")
                ||command.equals("-help")
                ||command.equals("--h")
                ||command.equals("--help");
    }
    public static String[] trimNull(String[] args){
        if(isEmpty(args)){
            return null;
        }
        List<String> results=new ArrayList<>();
        for(String str:args){
            if(!isEmpty(str)){
                results.add(str);
            }
        }
        return results.toArray(new String[0]);
    }
    public static boolean isEmpty(String[] args){
        if(args==null||args.length==0){
            return true;
        }
        for (String s:args){
            if(!isEmpty(s)){
                return false;
            }
        }
        return true;
    }
    public static boolean isEmpty(String str){
        if(str==null){
            return true;
        }
        str=str.trim();
        return str.length()==0;
    }

    public static void deleteDir(File dir){
        if(!dir.exists()){
            return;
        }
        if(dir.isFile()){
            dir.delete();
            return;
        }
        if(!dir.isDirectory()){
            return;
        }
        File[] files=dir.listFiles();
        if(files==null){
            deleteEmptyDirectories(dir);
            return;
        }
        for(File file:files){
            deleteDir(file);
        }
        deleteEmptyDirectories(dir);
    }
    public static void deleteEmptyDirectories(File dir){
        if(dir==null || !dir.isDirectory()){
            return;
        }
        File[] allFiles=dir.listFiles();
        if(allFiles==null || allFiles.length==0){
            dir.delete();
            return;
        }
        int len=allFiles.length;
        for(int i=0;i<len;i++){
            File file=allFiles[i];
            if(file.isDirectory()){
                deleteEmptyDirectories(file);
            }
        }
        allFiles=dir.listFiles();
        if(allFiles==null || allFiles.length==0){
            dir.delete();
        }
    }
    // This is to respect someone's protection from editing,
    // if you reach here be ethical do not patch it for distribution.
    public static String isProtected(ApkModule apkModule){
        Properties properties = null;
        try {
            TableBlock tableBlock = apkModule.getTableBlock();
            String str = loadApkEditorProperties(tableBlock);
            properties = loadApkEditorProperties(str);
        } catch (Exception exception) {
        }
        if(properties == null){
            return null;
        }
        String protect = properties.getProperty("EDIT_TYPE", null);
        if(protect==null || !protect.contains(EDIT_TYPE_PROTECTED)){
            return null;
        }
        return protect;
    }
    private static String loadApkEditorProperties(TableBlock tableBlock){
        if(tableBlock == null){
            return null;
        }
        TableStringPool stringPool = tableBlock.getTableStringPool();
        int count = stringPool.countStrings();
        TableString tableString = stringPool.get(count-1);
        return loadApkEditorProperties(tableString);
    }
    private static String loadApkEditorProperties(TableString tableString){
        if(tableString == null){
            return null;
        }
        String str = tableString.get();
        if(str==null || !(str.contains("REPO=") && str.contains(APKEditor.getRepo()))){
            return null;
        }
        return str;
    }
    private static Properties loadApkEditorProperties(String str){
        if(str==null || !(str.contains("REPO=") && str.contains(APKEditor.getRepo()))){
            return null;
        }
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(str));
            return properties;
        } catch (Exception exception) {
            return null;
        }
    }
    public static void addApkEditorInfo(ApkModule apkModule, String type){
        try {
            addApkEditorInfo(apkModule.getTableBlock(), type);
        } catch (Exception ignored) {
        }
    }
    private static void addApkEditorInfo(TableBlock tableBlock, String type){
        if(tableBlock == null){
            return;
        }
        TableStringPool stringPool = tableBlock.getTableStringPool();
        int count = stringPool.countStrings();
        if(count==0){
            return;
        }
        TableString tableString = stringPool.get(count - 1);
        if(!isApkEditorInfo(tableString)){
            StringArray<TableString> stringsArray = stringPool.getStringsArray();
            count = stringsArray.childesCount();
            stringsArray.ensureSize(count + 1);
            tableString = stringPool.get(count);
        }
        tableString.set(buildApkEditorInfo(type));
        IntegerItem dummyReference = new IntegerItem();
        dummyReference.set(tableString.getIndex());
        tableString.addReference(dummyReference);
    }
    private static String buildApkEditorInfo(String type){
        StringBuilder builder = new StringBuilder();
        builder.append("NAME=").append(APKEditor.getName());
        builder.append("\nVERSION=").append(APKEditor.getVersion());
        builder.append("\nREPO=").append(APKEditor.getRepo());
        builder.append("\nEDIT_TYPE=").append(type);
        builder.append("\nTIME=").append(System.currentTimeMillis());
        return builder.toString();
    }
    private static boolean isApkEditorInfo(TableString tableString){
        if(tableString==null){
            return false;
        }
        String str = tableString.getHtml();
        if(str==null){
            return false;
        }
        return str.contains("REPO=")
                && str.contains(APKEditor.getRepo());
    }

    public static final String EDIT_TYPE_PROTECTED = "PROTECTED";
}
