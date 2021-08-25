package com.example.uberdriver.Utils;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.uberdriver.Common.Common;
import com.example.uberdriver.Model.EventBus.NotifyToRiderEvent;
import com.example.uberdriver.Model.FCMSendData;
import com.example.uberdriver.Model.Token;
import com.example.uberdriver.R;
import com.example.uberdriver.Remote.IFCService;
import com.example.uberdriver.Remote.RetrofitFCMClient;
import com.example.uberdriver.Service.MyFirebaseMessaging;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserUtils {
    public static void updateUser(View view, Map<String, Object> updateData) {

        FirebaseDatabase.getInstance()
                .getReference(Common.user_driver_tbl)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Snackbar.make(view, "update information successfully!", Snackbar.LENGTH_SHORT).show();


            }
        });
    }

    public static void updateToken(Context context, String token) {

        Token tokenModel = new Token(token);

        FirebaseDatabase.getInstance()
                .getReference(Common.token_tbl)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

    }


    public static void sendDeclineRequest(View view, Context context, String key) {
        //copy from rider
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCService ifcService = RetrofitFCMClient.getInstance().create(IFCService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.token_tbl)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Token tokenModel = snapshot.getValue(Token.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_DECLINE);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for   action driver decline request");
                            notificationData.put(Common.RIDER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());

                            assert tokenModel != null;
                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);

                            compositeDisposable.add(ifcService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0) {
                                            compositeDisposable.clear();
                                            Snackbar.make(view, context.getString(R.string.decline_failed), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(view, context.getString(R.string.decline_success), Snackbar.LENGTH_LONG).show();

                                        }
                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendAcceptRequestToRider(View view, Context context, String key, String triNumberId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCService ifcService = RetrofitFCMClient.getInstance().create(IFCService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.token_tbl)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Token tokenModel = snapshot.getValue(Token.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_ACCEPT);
                            notificationData.put(Common.NOTI_CONTENT, "This message represent for   action driver accept request");
                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.TRIP_KEY, triNumberId);



                            assert tokenModel != null;
                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);

                            compositeDisposable.add(ifcService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0) {
                                            compositeDisposable.clear();
                                            Snackbar.make(view, context.getString(R.string.accept_failed), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(view, context.getString(R.string.Accept_success), Snackbar.LENGTH_LONG).show();

                                        }
                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendNotifyToRider(Context context, View view, String key) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCService ifcService = RetrofitFCMClient.getInstance().create(IFCService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.token_tbl)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Token tokenModel = snapshot.getValue(Token.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTI_TITLE, context.getString(R.string.driver_arrived));
                            notificationData.put(Common.NOTI_CONTENT, context.getString(R.string.your_driver_arrived));
                            notificationData.put(Common.DRIVER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.RIDER_KEY, key);



                            assert tokenModel != null;
                            FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);

                            compositeDisposable.add(ifcService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0) {
                                            compositeDisposable.clear();
                                            Snackbar.make(view, context.getString(R.string.accept_failed), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            EventBus.getDefault().postSticky(new NotifyToRiderEvent());
                                        }
                                    }, throwable -> {

                                        compositeDisposable.clear();
                                        Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }));

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendDeclineAndRemoveTripRequest(View view, Context context, String key, String triNumberId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCService ifcService = RetrofitFCMClient.getInstance().create(IFCService.class);


        //first remove trip from database
        FirebaseDatabase.getInstance()
                .getReference(Common.TRIP)
                .child(triNumberId)
                .removeValue()
                .addOnFailureListener(e -> {
                    Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(unused -> {
                    //Delete success,send notification to rider app

                    FirebaseDatabase
                            .getInstance()
                            .getReference(Common.token_tbl)
                            .child(key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
                                        Token tokenModel = snapshot.getValue(Token.class);

                                        Map<String, String> notificationData = new HashMap<>();
                                        notificationData.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP);
                                        notificationData.put(Common.NOTI_CONTENT, "This message represent for   action driver decline request");
                                        notificationData.put(Common.RIDER_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        assert tokenModel != null;
                                        FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);

                                        compositeDisposable.add(ifcService.sendNotification(fcmSendData)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(fcmResponse -> {
                                                    if (fcmResponse.getSuccess() == 0) {
                                                        compositeDisposable.clear();
                                                        Snackbar.make(view, context.getString(R.string.decline_failed), Snackbar.LENGTH_LONG).show();
                                                    } else {
                                                        Snackbar.make(view, context.getString(R.string.decline_success), Snackbar.LENGTH_LONG).show();

                                                    }
                                                }, throwable -> {

                                                    compositeDisposable.clear();
                                                    Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                                }));

                                    } else {
                                        compositeDisposable.clear();
                                        Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    compositeDisposable.clear();
                                    Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                });
    }
}
