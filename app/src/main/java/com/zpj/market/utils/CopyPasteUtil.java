//package com.zpj.sjly.utils;
//
//import android.content.Context;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.nio.channels.FileChannel;
//import java.util.logging.Handler;
//
//public class CopyPasteUtil {
//
//    private static long dirSize = 0;// 文件夹总体积
//    private static long hasReadSize = 0;// 已复制的部分，体积
//    private static CopyProgressDialog progressDialog;// 进度提示框
//    private static Thread copyFileThread;
//    private static Runnable run = null;
//    private static FileInputStream fileInputStream = null;
//    private static FileOutputStream fileOutputStream = null;
//    private static FileChannel fileChannelOutput = null;
//    private static FileChannel fileChannelInput = null;
//    private static Thread copyDirThread;
//    private static BufferedInputStream inbuff = null;
//    private static BufferedOutputStream outbuff = null;
//
//    /**
//     * handler用于在主线程刷新ui
//     */
//    private final static Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (msg.what == 0) {
//                int progress = msg.getData().getInt("progress");
//                long fileVolume = msg.getData().getLong("fileVolume");
//                progressDialog.setProgress(progress);
//                progressDialog.setProgressText(progress + "%");
//                progressDialog.setFileVolumeText(fileVolume * progress / 100 + " MB/" + fileVolume + " MB");
//            }else if(msg.what==1){
//                if(progressDialog != null) {
//                    int fileVolume = msg.getData().getInt("fileVolume");
//                    progressDialog.setFileVolumeText(0 + " MB/" + fileVolume + " MB");
//                }
//            }
//        };
//    };
//
//    /**
//     * 复制单个文件
//     */
//    public static boolean copyFile(final String oldPathName, final String newPathName, Context context) {
//        //大于50M时，才显示进度框
//        final File oldFile = new File(oldPathName);
//        if (oldFile.length() > 50 * 1024 * 1024) {
//            progressDialog = new CopyProgressDialog(context);
//            progressDialog.show();
//            progressDialog.setNameText(oldPathName);
//            progressDialog.setOnCancelListener(new OnCancelListener() {//点击返回取消时，关闭线程和流
//                @Override
//                public void onCancel(DialogInterface arg0) {
//                    run = null;
//                    copyFileThread.interrupt();
//                    copyFileThread = null;
//                    try {
//                        fileInputStream.close();
//                        fileOutputStream.close();
//                        fileChannelOutput.close();
//                        fileChannelInput.close();
//                    } catch (IOException e) {
//                        Log.e("CopyPasteUtil", "CopyPasteUtil copyFile error:" + e.getMessage());
//                    }
//                }
//            });
//        }
//        run = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    File fromFile = new File(oldPathName);
//                    File targetFile = new File(newPathName);
//                    fileInputStream = new FileInputStream(fromFile);
//                    fileOutputStream = new FileOutputStream(targetFile);
//                    fileChannelOutput = fileOutputStream.getChannel();
//                    fileChannelInput = fileInputStream.getChannel();
//                    ByteBuffer buffer = ByteBuffer.allocate(4096);
//                    long transferSize = 0;
//                    long size = new File(oldPathName).length();
//                    int fileVolume = (int) (size / 1024 /1024);
//                    int tempP = 0;
//                    int progress = 0;
//                    while (fileChannelInput.read(buffer) != -1) {
//                        buffer.flip();
//                        transferSize += fileChannelOutput.write(buffer);
//                        progress = (int) (transferSize * 100 / size);
//                        if(progress>tempP){
//                            tempP = progress;
//                            Message message = handler.obtainMessage(0);
//                            Bundle b = new Bundle();
//                            b.putInt("progress", progress);
//                            b.putLong("fileVolume", fileVolume);
//                            message.setData(b);
//                            handler.sendMessage(message);
//                        }
//                        buffer.clear();
//                    }
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    fileInputStream.close();
//                    fileChannelOutput.close();
//                    fileChannelInput.close();
//                    if(progressDialog.isShow()){
//                        progressDialog.dismiss();
//                    }
//                } catch (Exception e) {
//                    Log.e("CopyPasteUtil", "CopyPasteUtil copyFile error:" + e.getMessage());
//                }
//            }
//        };
//        copyFileThread = new Thread(run);
//        copyFileThread.start();
//        return true;
//    }
//
//    /**
//     * 复制文件夹
//     */
//    public static void copyDirectiory(final String sourceDir, final String targetDir, final Context context) {
//        if (context != null) {
//            if (dirSize > 50 * 1024 * 1024) {
//                progressDialog = new CopyProgressDialog(context);
//                progressDialog.show();
//                progressDialog.setNameText(sourceDir);
//                progressDialog.setOnCancelListener(new OnCancelListener() {//点击返回取消时，关闭线程和流
//                    @Override
//                    public void onCancel(DialogInterface arg0) {
//                        run = null;
//                        copyDirThread.interrupt();
//                        copyDirThread = null;
//                        try {
//                            if(fileInputStream != null) fileInputStream.close();
//                            if(fileOutputStream != null) fileOutputStream.close();
//                            if(inbuff != null) inbuff.close();
//                            if(outbuff != null) outbuff.close();
//                            if(fileChannelOutput != null) fileChannelOutput.close();
//                            if(fileChannelInput != null) fileChannelInput.close();
//                        } catch (IOException e) {
//                            Log.e("CopyPasteUtil", "CopyPasteUtil copyDirectiory error:" + e.getMessage());
//                        }
//                    }
//                });
//            }
//        }
//        run = new Runnable() {
//            @Override
//            public void run() {
//                (new File(targetDir)).mkdirs();
//                File[] file = (new File(sourceDir)).listFiles();// 获取源文件夹当下的文件或目录
//                for (int i = 0; i < file.length; i++) {
//                    if (file[i].isFile()) {
//                        File sourceFile = file[i];
//                        File targetFile = new File(
//                                new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());// 目标文件
//                        copyFile(sourceFile, targetFile);
//                    }
//                    if (file[i].isDirectory()) {
//                        String dir1 = sourceDir + "/" + file[i].getName();
//                        String dir2 = targetDir + "/" + file[i].getName();
//                        copyDirectiory(dir1, dir2, null);
//                    }
//                }
//
//            }
//        };
//        copyDirThread = new Thread(run);
//        copyDirThread.start();
//    }
//
//    /**
//     * 复制单个文件，用于上面的复制文件夹方法
//     *
//     * @param sourcefile
//     *            源文件路径
//     * @param targetFile
//     *            目标路径
//     */
//    public static synchronized void copyFile(final File sourcefile, final File targetFile) {
//        try {
//            fileInputStream = new FileInputStream(sourcefile);
//            inbuff = new BufferedInputStream(fileInputStream);
//            fileOutputStream = new FileOutputStream(targetFile);// 新建文件输出流并对它进行缓冲
//            outbuff = new BufferedOutputStream(fileOutputStream);
//            int fileVolume = (int) (dirSize / (1024 * 1024));
//            fileChannelOutput = fileOutputStream.getChannel();
//            fileChannelInput = fileInputStream.getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate(4096);
//            long transferSize = 0;
//            int tempP = 0;
//            int progress = 0;
//            while (fileChannelInput.read(buffer) != -1) {
//                buffer.flip();
//                transferSize += fileChannelOutput.write(buffer);
//                if (dirSize > 50 * 1024 * 1024) {
//                    progress = (int) (((transferSize + hasReadSize) * 100) / dirSize);
//                    if(progress>tempP){
//                        tempP = progress;
//                        Message message = handler.obtainMessage(0);
//                        Bundle b = new Bundle();
//                        b.putInt("progress", progress);
//                        b.putLong("fileVolume", fileVolume);
//                        message.setData(b);
//                        handler.sendMessage(message);
//                        if(progressDialog.isShow() && progress==100){
//                            progressDialog.dismiss();
//                        }
//                    }
//                }
//                buffer.clear();
//            }
//            hasReadSize += sourcefile.length();
//            outbuff.flush();
//            inbuff.close();
//            outbuff.close();
//            fileOutputStream.close();
//            fileInputStream.close();
//            fileChannelOutput.close();
//            fileChannelInput.close();
//        } catch (FileNotFoundException e) {
//            Log.e("CopyPasteUtil", "CopyPasteUtil copyFile error:" + e.getMessage());
//        } catch (IOException e) {
//            Log.e("CopyPasteUtil", "CopyPasteUtil copyFile error:" + e.getMessage());
//        }
//    }
//
//    /**
//     * 获取文件夹大小
//     * @param file
//     */
//    public static void getDirSize(File file) {
//        if (file.isFile()) {
//            // 如果是文件，获取文件大小累加
//            dirSize += file.length();
//        } else if (file.isDirectory()) {
//            File[] f1 = file.listFiles();
//            for (int i = 0; i < f1.length; i++) {
//                // 调用递归遍历f1数组中的每一个对象
//                getDirSize(f1[i]);
//            }
//        }
//    }
//
//    /**
//     * 初始化全局变量
//     */
//    public static void initDirSize() {
//        dirSize = 0;
//        hasReadSize = 0;
//    }
//
//    /**
//     * 复制文件夹前，初始化两个变量
//     */
//    public static void initValueAndGetDirSize(File file) {
//        initDirSize();
//        getDirSize(file);
//    }
//
//}
