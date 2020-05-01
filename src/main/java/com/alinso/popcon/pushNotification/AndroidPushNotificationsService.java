package com.alinso.popcon.pushNotification;


import com.alinso.popcon.entity.User;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AndroidPushNotificationsService {
    private static final String FIREBASE_SERVER_KEY = "AAAAmQVDFFQ:APA91bFWxzllW-UCzzet7Of9d2tM1ErllvVqJ17Nv5-iEhkfXIBNSLinc_SA272AJn4tKbJ_y1n5HdMQcPj17IOntda6RNPoO8H8L9Jni5fDHX1TsJug5nbJn4WhU3iXIhs8qUU8aofA";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";


    private void send(String title, String to, String message) {

        if(to==null)
            return;

        try {

            JSONObject body = new JSONObject();
            body.put("to", to);
            body.put("Content-Type", "application/json");
            body.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);

            body.put("notification", notification);
            HttpEntity<String> request = new HttpEntity<>(body.toString());


            //CompletableFuture<String> pushNotification = sendToFirebase(request);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            /**
             https://fcm.googleapis.com/fcm/send
             Content-Type:application/json
             Authorization:key=FIREBASE_SERVER_KEY*/

            ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
            interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));

            restTemplate.setInterceptors(interceptors);

            String firebaseRequest = restTemplate.postForObject(FIREBASE_API_URL, request, String.class);

            CompletableFuture<String> pushNotification = CompletableFuture.completedFuture(firebaseRequest);
            CompletableFuture.allOf(pushNotification).join();
            String firebaseResponse = pushNotification.get();

            System.out.println(firebaseResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Async
    public Boolean newMessage(User trigger, User target) {
        String message = trigger.getUsername()  + " sana bir mesaj gönderdi";
        send("Yeni Mesaj", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }
    @Async
    public Boolean newDuelRequest(User trigger, User target) {
        String message = trigger.getUsername()  + " seni düelloya davet ediyor";
        send("Meydan Okuma", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newDuelAccept(User trigger, User target) {
        String message = trigger.getUsername()  + " düello davetini kabul etti";
        send("Düello Başladı", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newDuelDecline(User trigger, User target) {
        String message = trigger.getUsername()  + " düello davetini reddetti";
        send("Düello Ret", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }
    @Async
    public Boolean newFollow(User trigger, User target) {
        String message = trigger.getUsername()  + " seni takip etti";
        send("Yeni Takipçi", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }
    @Async
    public Boolean newComment(User trigger, User target) {
        String message = trigger.getUsername()  + " fotoğrafına yorum yaptı";
        send("Yeni Yorum", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    @Async
    public Boolean newShowPercent(User trigger, User target) {
        String message = " Bir fotoğrafın için ilk puanlama belli oldu";
        send("İlk Sonuçlar", target.getFirebaseId(), message);
        if(target.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }

    public Boolean newDuelFinish(User watcher) {
        String message = "Takip ettiğin bir düello sonuçlandı";
        send("Düello Sonucu", watcher.getFirebaseId(), message);
        if(watcher.getFirebaseId()==null){
            return false;
        }
        else{
            return true;
        }
    }
}
