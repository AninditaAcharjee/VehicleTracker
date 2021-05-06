package com.jkkniu.vehicletracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;

import static com.jkkniu.vehicletracker.DataRefs.LOCATION_REF;
import static com.jkkniu.vehicletracker.Notification_Channel.CHANNEL_ID;

public class ForegroundNotificationService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int TRACK_NOTIFY_ID = 545;
    private PendingIntent pendingIntent;
    private LocationCallback locationCallback;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        p("service started");
        if (intent != null) {
            String action = intent.getAction();
            p(action);
            if (action != null) {
                switch (action) {
                    case "START_TRACKING": {

                        String busName = intent.getStringExtra("bus");
                        Intent notificationIntent = new Intent(this, MapDriver.class);
                        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle("Vehicle Tracker")
                                .setContentText("Tracking Started")
                                .setSmallIcon(R.drawable.driver_bus)
                                .setContentIntent(pendingIntent)
                                .build();
                        startForeground(TRACK_NOTIFY_ID, notification);

                        getLocation(busName, ForegroundNotificationService.this);
                        break;
                    }
                    case "STOP_TRACKING": {
                        stopForeground(true);
                        stopSelf();
                        stopSelf(TRACK_NOTIFY_ID);
                        stopSelfResult(TRACK_NOTIFY_ID);
                        if (locationCallback != null) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }

                        break;
                    }
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    //    stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getLocation(final String busName, Context context) {
        final long time = 4900;
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = new FusedLocationProviderClient(context);
        }

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        Location location = locationResult.getLastLocation();
                        Log.e("LOCATION Update", location.toString());
                        //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        String time=Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
                        saveLocationToServer(busName, location, time);

                    } else {
                        Log.e("LOCATION Update", "no updates");
                    }

                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }
            };
        }

        fusedLocationProviderClient
                .requestLocationUpdates(
                        new LocationRequest()
                                .setFastestInterval(time)
                                .setInterval(time)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                        locationCallback, Looper.getMainLooper()
                );
    }


    public void saveLocationToServer(String busName, Location location, String time) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(LOCATION_REF)
                .child(busName)
                .setValue(
                        new LocationSaver()
                                .setLatitude(location.getLatitude())
                                .setLongitude(location.getLongitude())
                                .setTimestamp(time)

                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "database e data paisi");
                        Toast.makeText(
                                ForegroundNotificationService.this,
                                "database e data paisi",
                                Toast.LENGTH_SHORT)
                                .show();
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(TRACK_NOTIFY_ID, updateNotification("update"));
                    }
                }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(TRACK_NOTIFY_ID, updateNotification(e.getMessage()));
                        Toast.makeText(ForegroundNotificationService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void p(String s) {
        Log.e("Track", s);
    }

    Notification updateNotification(String update) {
        Intent notificationIntent = new Intent(this, MapDriver.class);
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vehicle Tracker")
                .setContentText("Last " + update + " at " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.driver_bus)
                .build();


    }

}
