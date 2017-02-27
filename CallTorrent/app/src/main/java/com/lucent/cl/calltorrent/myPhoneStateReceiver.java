package com.lucent.cl.calltorrent;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * myPhoneStateReceiver monitor the phone state.
 * If incoming call, answer it and record it.
 * If outgoing call, record it.
 * Issue:
 * 1:the MediaRecorder.AudioSource.VOICE_CALL will fail on API18 and above.
 * 2:Answer the phone call by sent the Intent.ACTION_MEDIA_BUTTON only work on API 10 and lower.
 *
 * Future plan:
 * 1:sent the recording file out
 *
 */
public class myPhoneStateReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "PhoneStateReceiver";
    private static MediaRecorder mRecorder;
    private static String mFileName = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "BroadcastReceiver received");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean autoAnswerFlag = sharedPref.getBoolean("AutoAnswer", false);
        Boolean autoRecordFlag = sharedPref.getBoolean("AutoRecord", false);
        Boolean autoFWSMSFlag = sharedPref.getBoolean("AutoFWSMS", false);
        Log.d(LOG_TAG, "3 flas is"
                + "  " + autoAnswerFlag.toString()
                + "  " + autoRecordFlag.toString()
                + "  " + autoFWSMSFlag.toString());

        String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d(LOG_TAG, "phone is idle");
            if(autoRecordFlag)
                stopRecord();
        }else if(phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            if(autoRecordFlag)
                startRecord();
            Log.d(LOG_TAG, "phone is OFFHOOK");
        }else if(phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            if(autoAnswerFlag) {
                Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                context.sendOrderedBroadcast(meidaButtonIntent, null);
                Log.d(LOG_TAG, "Intent.ACTION_MEDIA_BUTTON sent out + " + phoneState);
            }
        }else{
            Log.d(LOG_TAG, "state unknown"+phoneState);
        }
    }

    private void startRecord(){
        Log.d(LOG_TAG, "startRecord");
        Date date = new Date();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName +="/LC_CALL/" ;
//        mFileName = "/mnt/internal/LC_CALL/";

        File file = new File(mFileName);
        if (!file.exists()) {
            file.mkdir();
        }

        mFileName += date.getTime();
        mFileName += ".3gp";
        Log.d(LOG_TAG, "mFileName is "+mFileName);

        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setOutputFile(mFileName);



        try {
            mRecorder.prepare();
        }catch(IllegalStateException e){
            Log.e(LOG_TAG, "prepare() failed for IllegalStateException");
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed for IOException");
        }

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){
            Log.e(LOG_TAG, "sleep() failed"+e);
        }

        try {
            mRecorder.start();
        } catch (IllegalStateException e){
            Log.e(LOG_TAG, "start() failed"+e);
        }
    }

    private void stopRecord(){
        Log.d(LOG_TAG, "stopRecord");
        if (null != mRecorder){
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }


    }
}