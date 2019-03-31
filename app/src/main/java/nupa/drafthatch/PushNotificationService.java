package nupa.drafthatch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class PushNotificationService extends GcmListenerService {
    private int notificationID = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        notificationID = notificationID + 1;
        String message = data.getString("message");
        Log.d("Mensaje entrante",message);
        //createNotification(mTitle, push_msg);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,new Intent(this, HatchesNotificationActivity.class),0);

        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.hatcheggicon)
                .setContentTitle("Around!")
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message))
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notificationID,mBuilder.build());
    }
}
