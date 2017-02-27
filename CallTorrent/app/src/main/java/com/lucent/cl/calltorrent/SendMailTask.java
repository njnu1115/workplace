package com.lucent.cl.calltorrent;

import android.os.AsyncTask;
import android.util.Log;


public class SendMailTask extends AsyncTask<String, Integer, Boolean> {
    private static final String LOG_TAG = "Call.SendMailTask";

    protected Boolean doInBackground(String... intentString) {
        String msg_from = intentString[0];
        String imapServer = intentString[1];
        String imapUser = intentString[2];
        String imapPassword = intentString[3];
        String myPhoneNo = intentString[4];
        String toAddress = intentString[5];
        String msgBody = intentString[6];
        Log.d(LOG_TAG, imapServer + imapPassword + imapUser + myPhoneNo + toAddress);

        MailSender mailsender = new MailSender(imapServer, imapUser, imapPassword);
        mailsender.setSubject(msg_from + "to" + myPhoneNo);
        mailsender.setBody(msgBody);
        String[] toArr = {toAddress};
        mailsender.setTo(toArr);
        try {
            mailsender.send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Boolean result) {
//        showNotification("Downloaded " + result + " bytes");
    }
}
