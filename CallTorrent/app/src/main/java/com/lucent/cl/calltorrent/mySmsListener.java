package com.lucent.cl.calltorrent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class mySmsListener extends BroadcastReceiver {
    private static final String LOG_TAG = "mySmsListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean autoFWSMSFlag = sharedPref.getBoolean("AutoFWSMS", false);
        if (!autoFWSMSFlag) {
            return;
        }
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            String imapServer = sharedPref.getString("SMTPserver", "imap.qq.com");
            String imapUser = sharedPref.getString("SMTPusername", "");
            String imapPassword = sharedPref.getString("SMTPpswd", "");
            String myPhoneNo = sharedPref.getString("myPhoneNumber", "");
            String toAddress = sharedPref.getString("toAddress", "");
            String sender = new String();
            StringBuffer content = new StringBuffer();
            for (Object pdu : pdus) {
                byte[] data = (byte[]) pdu;
                final int sdk = Build.VERSION.SDK_INT;
                SmsMessage message;
                if (sdk < Build.VERSION_CODES.M) {
                    message = SmsMessage.createFromPdu(data);
                } else {
                    message = SmsMessage.createFromPdu(data, intent.getStringExtra("format"));
                }
                sender = message.getOriginatingAddress();  // 获取短信的发送者
                content.append(message.getMessageBody());  // 获取短信的内容
            }
            new SendMailTask().execute(sender, imapServer, imapUser, imapPassword, myPhoneNo, toAddress, content.toString());
        }
    }
}