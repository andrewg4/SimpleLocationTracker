package ua.com.prologistic.runtracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Andrew on 06.03.2016.
 */
public class RunManager {

    private static final String TAG = "RunManager";
    public static final String ACTION_LOCATION = "ua.com.prologistic.runtracker.ACTION_LOCATION";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    /*// для теста положения
    private static final String TEST_PROVIDER = "TEST_PROVIDER";*/

    // Закрытый конструктор заставляет использовать
    // RunManager.get(Context)
    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static RunManager get(Context c) {
        if (sRunManager == null) {
            // Использование контекста приложения для предотвращения
            // утечки активностей
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates(Context context) {
        String provider = LocationManager.GPS_PROVIDER;
        /*// TODO: Для теста!
        // Если имеется поставщик тестовых данных и он активен,
        // использовать его.
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);*/


        // Получение последнего известного местоположения
        // и его рассылка (если данные доступны).
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // Время инициализируется текущим значением
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
//        Запуск обновлений из LocationManager
        PendingIntent pi = getLocationPendingIntent(true);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

}
