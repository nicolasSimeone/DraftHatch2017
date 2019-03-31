package nupa.drafthatch;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebastianfaro on 19/11/15.
 */


public class AdapterAprobacion extends ArrayAdapter<Hatch>{

    private LayoutInflater inflater;
    public List<Hatch> Hatchs;
    private ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

    public AdapterAprobacion(Activity activity, List<Hatch> Hatchs){
        super(activity, R.layout.lista_notificaciones, Hatchs);
        this.Hatchs=Hatchs;


    inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){

        final Hatch Hatchs1= Hatchs.get(position);
        convertView=inflater.inflate(R.layout.lista_notificaciones, parent, false);

        TextView Header=(TextView)convertView.findViewById(R.id.textTitulo);
        Header.setText(Hatchs1.getTitle());

        //region Manejo de Fechas
        Date dt = new Date();
        Date date1= new Date();
        Date date2= new Date();

        String toDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dt);
        String HatchDate=Hatchs1.getFecha();



        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date1=dateFormat.parse(toDateFormat);
            date2=dateFormat.parse(HatchDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = date2.getTime()-date1.getTime();
        long elapsedDays = TimeUnit.MILLISECONDS.toDays(different);
        long elapsedHours= TimeUnit.MILLISECONDS.toHours(different);
        long elapsedMinutes=TimeUnit.MILLISECONDS.toMinutes(different);

        String strDifference;

        TextView Fecha=(TextView)convertView.findViewById(R.id.txtFecha);

        if (elapsedDays>0){
            strDifference= Long.toString(elapsedDays);
            Fecha.setText(strDifference+" dias");
        }
        if (elapsedHours<24){
            strDifference= Long.toString(elapsedHours);
            Fecha.setText(strDifference+" horas");
        }
        if (elapsedMinutes<60){
            strDifference= Long.toString(elapsedMinutes);
            Fecha.setText(strDifference+" minutos");
        }
        //endregion

        //region Manejo de distancia
        ImageView Categoria=(ImageView)convertView.findViewById(R.id.imgCategoria);

        if(Hatchs1.getCategoria().equals("Musica")){
            Categoria.setImageResource(R.drawable.feed_musica);
        }

        if(Hatchs1.getCategoria().equals("Deportes")){
            Categoria.setImageResource(R.drawable.feed_deportes);
        }

        if(Hatchs1.getCategoria().equals("Activismo")){
            Categoria.setImageResource(R.drawable.feed_activismo);
        }

        if(Hatchs1.getCategoria().equals("Salidas")){
            Categoria.setImageResource(R.drawable.feed_salidas);
        }

        if(Hatchs1.getCategoria().equals("Flash")){
            Categoria.setImageResource(R.drawable.feed_flash);
        }

        if(Hatchs1.getCategoria().equals("Mascotas")){
            Categoria.setImageResource(R.drawable.feed_mascotas);
        }

         TextView descripcion=(TextView)convertView.findViewById(R.id.txtDescripcion);
         descripcion.setText("El usuario " + Hatchs1.getUsername()+ " quiere unirse!");


        return convertView;

    }
}
