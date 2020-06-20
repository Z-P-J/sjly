package io.haydar.filescanner;

import android.annotation.SuppressLint;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Haydar
 * @Package io.haydar.filescanner
 * @DATE 2017-04-24
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocalFileCacheManagerTest {
    public static final String TAG = "LocalFileCacheManagerTest";
    private long mStartTime, mEndTime;

    @Before
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Test
    public void startAllScan() {
       // LocalFileCacheManager.getInstance(InstrumentationRegistry.getTargetContext() ).scanDirAndSaveToDb(Environment.getExternalStorageDirectory().getAbsolutePath(), WHAT_SCAN_UPDATE);
    }

   // @Test
   public void isNeedToScannerAllTest() {
        LocalFileCacheManager.getInstance(InstrumentationRegistry.getTargetContext()).isNeedToScannerAll();
    }

   // @Test
    public void updateFilesTest(){
        LocalFileCacheManager.getInstance(InstrumentationRegistry.getTargetContext()).updateFiles();

    }

   // @Test
    public void updateDirsListTest(){
        LocalFileCacheManager.getInstance(InstrumentationRegistry.getTargetContext()).updateDirsList(null);

    }

    @SuppressLint("LongLogTag")
    @After
    public void end() {
        mEndTime = System.currentTimeMillis();
        Log.i(TAG, "运行时间=" + (mEndTime - mStartTime) + "ms");
    }
}
