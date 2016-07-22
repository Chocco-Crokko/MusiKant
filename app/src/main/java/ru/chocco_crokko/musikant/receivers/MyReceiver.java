package ru.chocco_crokko.musikant.receivers;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import ru.chocco_crokko.musikant.R;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ifHeadsetPluggedIn(intent))
            return;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);

        setButtonListener(context, "ru.yandex.music", views, R.id.musicButton);
        setButtonListener(context, "ru.yandex.radio", views, R.id.radioButton);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.kant)
                .setContent(views);

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(context);

        manager.notify(42, builder.build());
    }

    private void setButtonListener(Context context, String packageName, RemoteViews views, int buttonId) {
        Intent intent = getIntentToOpenAppOrMarket(context.getPackageManager(), packageName);
        views.setOnClickPendingIntent(buttonId, PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    private Intent getIntentToOpenAppOrMarket(PackageManager packageManager, String packageName) {
        if (isInstalled(packageManager, packageName)) {
            return packageManager.getLaunchIntentForPackage(packageName);
        } else {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        }
    }

    private boolean isInstalled(PackageManager packageManager, String packageName) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private boolean ifHeadsetPluggedIn(Intent intent) {
        return (intent.getIntExtra("state", 0) == 1);
    }


}
