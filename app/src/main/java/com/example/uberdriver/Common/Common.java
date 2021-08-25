package com.example.uberdriver.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.uberdriver.Model.User;
import com.example.uberdriver.R;
import com.example.uberdriver.Remote.FCMClient;
import com.example.uberdriver.Remote.IFCService;
import com.example.uberdriver.Remote.IGoogleAPI;
import com.example.uberdriver.Remote.RetrofitClient;
import com.example.uberdriver.Service.MyFirebaseMessaging;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
  

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInformation";
    public static final String user_rider_tbl = "RidersInformation";
    public static final String pivkup_request_tbl = "PickupRequest";
    public static final String NOTI_TITLE ="title" ;
    public static final String NOTI_CONTENT = "body";
    public static final String RIDER_PICKUP_LOCATION = "PickupLocation";
    public static final String RIDER_KEY = "RiderKey";
    public static final String REQUEST_DRIVER_TITLE = "RequestDriver";
    public static final String REQUEST_DRIVER_DECLINE ="Decline" ;
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickupLocationString";
    public static final String RIDER_DESTINATION_STRING ="DestinationLocationString" ;
    public static final String RIDER_DESTINATION = "DestinationLocation";
    public static final String RIDER_INFO ="Riders" ;
    public static final String TRIP ="Trips" ;
    public static final String REQUEST_DRIVER_ACCEPT = "Accept" ;
    public static final String DRIVER_KEY = "DriverKey";
    public static final String TRIP_KEY ="TripKey" ;
    public static final String TRIP_PICKUP_REF ="TripPickupLocation" ;
    public static final double MIN_RANGE_PICKU_IN_KM = 0.05; //50m
    public static final int WAIT_TIME_IN_MIN = 1;
    public static final String TRIP_DESTINATION_LOCATION_REF ="TripDestinationLocation" ;
    public static final String REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP = "DeclineRequestRemoveTrip";

    public static User currentUser;


    public static final String DRIVERS_LOCATION_REFERENCE="DriversLocation";

    public static String buildWelcomeMessage(){
        if (Common.currentUser !=null){
            return new StringBuilder("Welcome")
                    .append(Common.currentUser.getName())
                    .append("").toString();
        }else
            return "";
    }

    public static Location lastLocation=null;

    public static final String token_tbl="Tokens";

    public static final String baseURL="https://maps.googleapis.com";
    public static final String fcmURL="https://fcm.googleapis.com/";


    public static IFCService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCService.class);
    }

    //DECODE POLY
    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }


    public static void showNtification(Context context, int id, String title, String body, Intent i) {
        PendingIntent pendingIntent=null;
        if (i!=null){
            pendingIntent=PendingIntent.getActivity(context,id,i,PendingIntent.FLAG_UPDATE_CURRENT);
            String NOTIFICATION_CHANNEL_ID="uber_remake";
            NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "uber remake",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Uber Remake");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
                notificationChannel.enableVibration(true);

                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.ic_baseline_directions_car_24)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_baseline_directions_car_24));

            if (pendingIntent !=null){
                builder.setContentIntent(pendingIntent);
            }

            Notification notification=builder.build();
            notificationManager.notify(id,notification);
        }
    }

    public static String createUniqueTripNumber(long timeOffset) {
        Random random=new Random();
        Long current=System.currentTimeMillis()+timeOffset;
        Long unique=current+random.nextLong();
        if (unique <0) unique*=(-1);
        return String.valueOf(unique);
    }
}
