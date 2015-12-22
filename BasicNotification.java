import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class BasicNotification {

    /*
    *
    * Essa classe serve para facilitar a criação de notificações em apps.
    *
    * */

    public String bundle[];
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private Class activityClass, parentActivityClass;
    private boolean autoCancel;
    private int controlNumber;
    private boolean fixed;

    public BasicNotification(Context c) {
        this.context = c;

        activityClass = null;

        parentActivityClass = null;

        autoCancel = true;

        controlNumber = 1;

        builder = new NotificationCompat.Builder(context);

        bundle = new String[]{};

        fixed = false;
    }

    @SuppressWarnings("unused")
    public void setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
    }

    public void setBundle(String bundle[]) {
        this.bundle = bundle;
    }

    public void setActivity(Class activityClass) {
        this.activityClass = activityClass;
    }

    @SuppressWarnings("unused")
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public void setParentActivityClass(Class activityClass) {
        this.parentActivityClass = activityClass;
    }

    public void createBasicNotification(final int icon, final String title,
                                        final String text, final String tickerText) {

        builder.setAutoCancel(autoCancel);

        notification = builder.setSmallIcon(icon).setContentTitle(title)
                .setContentText(text).build();


        notification.tickerText = tickerText;

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (!fixed)
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        else
            notification.flags |= Notification.FLAG_NO_CLEAR;

        if (activityClass != null) {

            Intent intent = new Intent(context, activityClass);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            if (bundle.length > 0) {
                Bundle b = new Bundle();

                for (int i = 0; i < bundle.length; i += 2) {
                    b.putString(bundle[i], bundle[i + 1]);
                }

                intent.putExtras(b);
            }

            TaskStackBuilder stack = TaskStackBuilder.create(context);

            if (parentActivityClass != null)
                stack.addParentStack(activityClass);

            stack.addNextIntent(intent);

            PendingIntent pendingIntent = stack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setLatestEventInfo(context, title, text, pendingIntent);
        }

        notificationManager.notify(controlNumber, notification);
    }

    public Notification getNotification() {
        return notification;
    }

    public void cancel() {
        if (notificationManager != null)
            notificationManager.cancel(controlNumber);
    }

    public void setVibrationPattern(long vib[]) {
        builder.setVibrate(vib);
    }

    public void updateNotificationTicker(String text) {
        notification.tickerText = text;
        notificationManager.notify(controlNumber, notification);
    }

    public void setControlNumber(int controlNumber) {
        this.controlNumber = controlNumber;
    }
}
