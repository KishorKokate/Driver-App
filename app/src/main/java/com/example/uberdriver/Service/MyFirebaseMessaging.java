package com.example.uberdriver.Service;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uberdriver.Common.Common;
import com.example.uberdriver.Model.EventBus.DriverRequestReceived;
import com.example.uberdriver.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            UserUtils.updateToken(this,s);
        }
        if (!s.isEmpty()) {
          //  updateTokenServer(s);

        }
    }


 /*   private void updateTokenServer(String s) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) //if already login ,must update token
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
*/
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> dataRecv=remoteMessage.getData();
        if (dataRecv!=null){

            if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE))
            {
                DriverRequestReceived driverRequestReceived=new DriverRequestReceived();
                driverRequestReceived.setKey(dataRecv.get(Common.RIDER_KEY));
                driverRequestReceived.setPickupLocation(dataRecv.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestReceived.setPickupLocationString(dataRecv.get(Common.RIDER_PICKUP_LOCATION_STRING));

                driverRequestReceived.setDestinationLocation(dataRecv.get(Common.RIDER_DESTINATION));
                driverRequestReceived.setDestinationLocationString(dataRecv.get(Common.RIDER_DESTINATION_STRING));

                EventBus.getDefault().postSticky(driverRequestReceived);
            }

            Common.showNtification(this,new Random().nextInt(),
                    dataRecv.get(Common.NOTI_TITLE),
                    dataRecv.get(Common.NOTI_CONTENT),
                    null);
        }



    }


}
