package com.constantlab.statistics.background;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.MainActivity;


public class ReminderNotification {

    private static final String NOTIFICATION_TAG = "NewMessage";


    public static void notify(final Context context) {
        final Resources res = context.getResources();

        final String title = res.getString(R.string.app_name);
        final String text = "Text";// getNextString(context);
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(
//                        PendingIntent.getActivity(context, 0, intent, 0))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                )

                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(NOTIFICATION_TAG.hashCode(), notification);

    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG.hashCode());

    }

//    public static String getNextString(Context context) {
//        List<String> texts = Arrays.asList(context.getResources().getStringArray(R.array.notify_texts));
//        int notifyID = SharedPrefsService.getInstance().getNotifiTextId(context);
//        String text = "";
//        if (notifyID == texts.size()) {
//            notifyID = 0;
//        }
//        text = texts.get(notifyID);
//        notifyID++;
//        SharedPrefsService.getInstance().saveNotifiTexID(context, notifyID);
//
//
//        return text;
//    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_sync : R.mipmap.ic_launcher;
    }

}
