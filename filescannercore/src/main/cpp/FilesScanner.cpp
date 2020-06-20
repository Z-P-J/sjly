//
// Created by gjy on 2017/4/14.
//

#include "FilesScanner.h"
#include <stdio.h>
#include <android/log.h>
#include <sys/stat.h>
#include <dirent.h>
#include <string.h>

#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, "FileScanner::", __VA_ARGS__))

jobject list_obj;//目录的list
jmethodID list_add;//目录list的add方法
jclass fileInfo_cls;//目录文件信息实体类
jclass list_cls;
jmethodID fileInfo_constructor;//目录文件实体类的构造方法
jmethodID fileInfo_setFilePath;//目录文件实体类的setPath方法
jmethodID fileInfo_setLastModifyTime;//目录文件实体类的setPath方法
jmethodID fileInfo_setFileSize;//目录文件实体类的setFileSize方法

/**
 * 扫描文件夹
 * @param env
 * @param path
 */
void doScannerDirs(JNIEnv *env, char *path) {
    struct stat statBuffer;
    if (stat(path, &statBuffer) != 0) { //读取stat信息
        return;

    }
    if (!S_ISDIR(statBuffer.st_mode)) { //判断是否是目录
        return;
    }
    jobject fileInfo_obj = env->NewObject(fileInfo_cls,
                                          fileInfo_constructor);  //new一个FileInfo实体类对象
    jstring str = env->NewStringUTF(path);
    env->CallVoidMethod(fileInfo_obj, fileInfo_setFilePath, str);   //set地址
    // LOGI("时间---%d",statBuffer.st_mtim);
    jlong time = statBuffer.st_mtim.tv_sec;
    env->CallVoidMethod(fileInfo_obj, fileInfo_setLastModifyTime, time);//set最后一次修改的时间
    env->CallBooleanMethod(list_obj, list_add, fileInfo_obj);   //添加到目录list中
    env->DeleteLocalRef(fileInfo_obj);  //释放fileinfo实体
    env->DeleteLocalRef(str);//释放字符串
    struct dirent *entry;
    DIR *dir = opendir(path);
    if (!dir) {
        LOGE("Error opening directory '%s'", path);
        return;
    }

    while ((entry = readdir(dir)) != NULL) {//循环目录中的文件
        const char *name = entry->d_name;
        if (name[0] == '.' || name[1] == '.') {
            continue;
        }
        int type = entry->d_type;
        if (type != DT_DIR) {
            continue;
        }
        char dirPath[250];
        strcpy(dirPath, path);
        strcat(dirPath, "/");
        strcat(dirPath, name);
        doScannerDirs(env, dirPath);//递归循环子目录
    }
    closedir(dir);
}


/**
 * 扫描文件夹
 * @param env
 * @param path
 */
void doScannerUpdateDirs(JNIEnv *env, char *path) {
    struct dirent *entry;
    DIR *dir = opendir(path);
    if (!dir) {
        LOGE("Error opening directory '%s'", path);
        return;
    }
    while ((entry = readdir(dir)) != NULL) {//循环目录中的文件
        const char *name = entry->d_name;
        if (name[0] == '.' || name[1] == '.') {
            continue;
        }
        int type = entry->d_type;
        if (type != DT_DIR) {
            continue;
        }
        char dirPath[250];
        strcpy(dirPath, path);
        strcat(dirPath, "/");
        strcat(dirPath, name);
        struct stat statBuffer;
        if (stat(dirPath, &statBuffer) != 0) { //读取stat信息
            continue;

        }
        if (!S_ISDIR(statBuffer.st_mode)) { //判断是否是目录
            continue;
        }
        jobject fileInfo_obj = env->NewObject(fileInfo_cls,
                                              fileInfo_constructor);  //new一个FileInfo实体类对象
        jstring str = env->NewStringUTF(path);
        env->CallVoidMethod(fileInfo_obj, fileInfo_setFilePath, str);   //set地址
        jlong time = statBuffer.st_mtim.tv_sec;
        env->CallVoidMethod(fileInfo_obj, fileInfo_setLastModifyTime, time);//set最后一次修改的时间
        env->CallBooleanMethod(list_obj, list_add, fileInfo_obj);   //添加到目录list中
        env->DeleteLocalRef(fileInfo_obj);  //释放fileinfo实体
        env->DeleteLocalRef(str);//释放字符串
    }
    closedir(dir);
}


/**
 * 扫描文件
 * @param env
 * @param path
 */
void doScannerFiles(JNIEnv *env, char *path, char *typeStr) {
    struct dirent *entry;
    DIR *dir = opendir(path);
    if (!dir) {
        LOGE("Error opening directory '%s'", path);
        return;
    }
    while ((entry = readdir(dir)) != NULL) {
        if (DT_REG != entry->d_type) {
            continue;
        }
        char *name = entry->d_name;
        if (name[0] == '.') {
            continue;
        }
        //file名字小于5的忽略
        if (strlen(name) < 5) {

            continue;
        }
        //非.mp3格式的忽略
        char *extension = strrchr(name, '.');
        if (extension == NULL) {
            continue;
        }
        if (strcmp(typeStr, extension) != 0) {
            continue;
        }

        char dirPath[250];
        strcpy(dirPath, path);
        strcat(dirPath, "/");
        strcat(dirPath, name);
        struct stat statBuffer;
        if (stat(dirPath, &statBuffer) != 0) { //读取stat信息
            continue;
        }
        if (!S_ISREG(statBuffer.st_mode)) {
            continue;
        }
        jobject fileInfo_obj = env->NewObject(fileInfo_cls,
                                              fileInfo_constructor);  //new一个FileInfo实体类对象
        jstring str = env->NewStringUTF(dirPath);
        jlong time = statBuffer.st_mtim.tv_sec;
        jlong size = statBuffer.st_size;
        env->CallVoidMethod(fileInfo_obj, fileInfo_setFilePath, str);   //set地址
        env->CallVoidMethod(fileInfo_obj, fileInfo_setLastModifyTime, time);//set最后一次修改的时间
        env->CallVoidMethod(fileInfo_obj, fileInfo_setFileSize, size);//set文件的大小
        env->CallBooleanMethod(list_obj, list_add, fileInfo_obj);   //添加到目录list中
        env->DeleteLocalRef(fileInfo_obj);  //释放fileinfo实体
        env->DeleteLocalRef(str);//释放字符串
    }
    closedir(dir);
}


/**
 * 扫描完成之后做的回收工作
 * @param env
 */
void finish(JNIEnv *env) {
    env->DeleteLocalRef(list_cls);
    env->DeleteLocalRef(fileInfo_cls);
}

/**
 * 初始化
 * @param env
 */
void init(JNIEnv *env) {
    list_cls = env->FindClass("java/util/ArrayList");//获得ArrayList类引用
    jmethodID list_constructor = env->GetMethodID(list_cls, "<init>", "()V"); //获得构造函数
    list_obj = env->NewObject(list_cls, list_constructor);//new一个arrayList对象
    list_add = env->GetMethodID(list_cls, "add", "(Ljava/lang/Object;)Z");

    fileInfo_cls = env->FindClass(
            "io/haydar/filescanner/FileInfo");//获得FileInfo类引用
    fileInfo_constructor = env->GetMethodID(fileInfo_cls, "<init>", "()V"); //获得构造函数
    fileInfo_setFilePath = env->GetMethodID(fileInfo_cls, "setFilePath",
                                            "(Ljava/lang/String;)V");
    fileInfo_setLastModifyTime = env->GetMethodID(fileInfo_cls, "setLastModifyTime",
                                                  "(J)V");
    fileInfo_setFileSize = env->GetMethodID(fileInfo_cls, "setFileSize",
                                            "(J)V");
}

/**
 * 扫描指定格式的文件
 * @param env
 * @param thiz
 * @param str
 * @return
 */
jobject JNICALL Java_io_haydar_filescanner_FileScannerJni_scanFiles
        (JNIEnv *env, jclass thiz, jstring str, jstring type) {
    char *path = (char *) env->GetStringUTFChars(str, NULL);
    char *typeStr = (char *) env->GetStringUTFChars(type, NULL);
    init(env);
    doScannerFiles(env, path, typeStr);
    finish(env);
    env->ReleaseStringUTFChars(str, path);
    env->ReleaseStringUTFChars(type, typeStr);
    return list_obj;
}


/**
 * JNI扫描文件夹
 * @param env
 * @param thiz
 * @param str
 * @return
 */
jobject JNICALL Java_io_haydar_filescanner_FileScannerJni_scanDirs
        (JNIEnv *env, jclass thiz, jstring str) {
    char *path = (char *) env->GetStringUTFChars(str, NULL);
    init(env);
    doScannerDirs(env, path);
    finish(env);
    env->ReleaseStringUTFChars(str, path);
    return list_obj;
}


/**
 * JNI增量扫描文件夹
 * @param env
 * @param thiz
 * @param str
 * @return
 */
jobject JNICALL Java_io_haydar_filescanner_FileScannerJni_scanUpdateDirs
        (JNIEnv *env, jclass thiz, jstring str) {
    char *path = (char *) env->GetStringUTFChars(str, NULL);
    init(env);
    doScannerUpdateDirs(env, path);
    finish(env);
    env->ReleaseStringUTFChars(str, path);
    return list_obj;
}


jlong JNICALL Java_io_haydar_filescanner_FileScannerJni_getFileLastModifiedTime
        (JNIEnv *env, jclass thiz, jstring str) {
    char *path = (char *) env->GetStringUTFChars(str, NULL);
    struct stat statBuffer;
    if (stat(path, &statBuffer) != 0) { //读取stat信息
        return -1l;
    }
    jlong time = statBuffer.st_mtim.tv_sec;
    env->ReleaseStringUTFChars(str, path);
    return time;
}




