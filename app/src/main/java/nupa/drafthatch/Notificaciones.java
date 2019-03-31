package nupa.drafthatch;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sebastianfaro on 30/12/16.
 */

public class Notificaciones {

    public Context context;
    private int notificationID;
    private String mensaje;

    public Notificaciones(Context ctx){
         context = ctx;
    }

    public void sendNotification(final String registrationID, final String accion, final String Usuario, final String HatchTitle){


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest strreq = new StringRequest(Request.Method.POST,
                "http://nupa.com.ar/send_notification.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        // get response
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }){@Override
        public Map<String, String> getParams(){
            Map<String, String> params = new HashMap<>();

            switch (accion){
                case ("Union"):
                    mensaje = "El usuario "+ Usuario + " se ha unido a su evento " + HatchTitle;
                    params.put("message", mensaje);
                    break;
                case ("CancelacionUsuario"):
                    mensaje =  "El usuario "+ Usuario + " se ha dado de baja a su evento " + HatchTitle;
                    params.put("message", mensaje);
                    break;
                case ("CancelacionCreador"):
                    mensaje =  "El usuario "+ Usuario + " cancelo el evento " + HatchTitle;
                    params.put("message", mensaje);
            }

                params.put("registrationId", registrationID);

            return params;
        }
        };

        queue.add(strreq);

        final  DatabaseReference getNotificationID = FirebaseDatabase.getInstance().getReference().child("settings").child("NotificationID");
        getNotificationID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                if(message ==null){
                    getNotificationID.setValue("0");
                }
                else{

                    notificationID=Integer.parseInt(message);
                    notificationID=notificationID+1;
                    getNotificationID.setValue(String.valueOf(notificationID));

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Notificaciones");
                    DatabaseReference notification = mDatabase.child(String.valueOf(notificationID));
                    notification.child("IdNotification").setValue(String.valueOf(notificationID));
                    notification.child("Receptor").setValue(registrationID);
                    notification.child("Remitente").setValue(Usuario);
                    notification.child("Leido").setValue(0);
                    notification.child("Mensaje").setValue(mensaje);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
