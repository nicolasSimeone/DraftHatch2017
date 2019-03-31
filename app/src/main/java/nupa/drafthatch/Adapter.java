package nupa.drafthatch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import static nupa.drafthatch.R.id.view;

/**
 * Created by sebastianfaro on 19/11/15.
 */


public class Adapter extends ArrayAdapter<Hatch>{

    private LayoutInflater inflater;
    public List<Hatch> Hatchs;




    public Adapter(Activity activity, List<Hatch> Hatchs){
        super(activity, R.layout.filas, Hatchs);
        this.Hatchs=Hatchs;



    inflater = activity.getWindow().getLayoutInflater();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView=inflater.inflate(R.layout.filas, parent, false);
        Hatch Hatchs1= Hatchs.get(position);

        TextView Header=(TextView)convertView.findViewById(R.id.textTitulo);
        Header.setText(Hatchs1.getTitle());

        //region Manejo de Fechas
        Date dt = new Date();
        Date date1= new Date();
        Date date2= new Date();
        Date date3= new Date();
        Date date4= new Date();

        date4.setHours(0);
        date4.setMinutes(0);
        date4.setSeconds(0);

       // String toDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dt);
        String toDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(dt);

        String HatchDate=Hatchs1.getFechaInicio();
        String HatchEndDate=Hatchs1.getFechaFin();


        Calendar fechaHoy= Calendar.getInstance();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
           date1=dateFormat.parse(toDateFormat);
            date2=dateFormat.parse(HatchDate);
            date3=dateFormat.parse(HatchEndDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = date2.getTime()-date1.getTime();
         long endDate = date3.getTime();
        //long endDate = date3.getTime()-date1.getTime();
        long elapsedDays = TimeUnit.MILLISECONDS.toDays(different);
        long elapsedHours= TimeUnit.MILLISECONDS.toHours(different);
        long elapsedMinutes=TimeUnit.MILLISECONDS.toMinutes(different);
        long elapsedSeconds=TimeUnit.MILLISECONDS.toSeconds(different);

        long elapsedDaysEnd = TimeUnit.MILLISECONDS.toDays(endDate);
        long elapsedHoursEnd= TimeUnit.MILLISECONDS.toHours(endDate);
        long elapsedMinutesEnd=TimeUnit.MILLISECONDS.toMinutes(endDate);

        long elapsedDaysToday = TimeUnit.MILLISECONDS.toDays(date4.getTime());
        long elapsedHoursToday= TimeUnit.MILLISECONDS.toHours(date4.getTime());
        long elapsedMinutesToday=TimeUnit.MILLISECONDS.toMinutes(date4.getTime());

        String strDifference;

        TextView Fecha=(TextView)convertView.findViewById(R.id.txtFecha);

        if (elapsedDays>0){
            strDifference= Long.toString(elapsedDays);
            Fecha.setText(strDifference+" dias");
        }
        if (elapsedDays==1){
            strDifference= Long.toString(elapsedDays);
            Fecha.setText(strDifference+" dia");
        }
        if (elapsedHours<24){
            strDifference= Long.toString(elapsedHours);
            Fecha.setText(strDifference+" horas");
        }
        if (elapsedHours==1){
            strDifference= Long.toString(elapsedHours);
            Fecha.setText(strDifference+" hora");
        }
        if (elapsedMinutes<60){
            strDifference= Long.toString(elapsedMinutes);
            Fecha.setText(strDifference+" minutos");
        }
        if (elapsedMinutes==1){
            strDifference= Long.toString(elapsedMinutes);
            Fecha.setText(strDifference+" minuto");
        }
        if (elapsedMinutes==0){
           Fecha.setText("Comenzando!");
        }
        if (elapsedDays<0 || elapsedHours <0 || elapsedMinutes <0 ){
            if((elapsedDaysToday>elapsedDays && elapsedDaysToday<elapsedDaysEnd) &&(elapsedMinutesToday>elapsedMinutes && elapsedMinutesToday<elapsedMinutesEnd) &&(elapsedHoursToday>elapsedHours && elapsedHoursToday<elapsedHoursEnd )) {
                Fecha.setText("En Progreso!");
            }else{

                Fecha.setText("Terminado!");
            }
        }
        if (Hatchs1.getEstado().equals("Cerrado")){
            Fecha.setText("Terminado!");
        }




        //endregion

        //region Manejo de distancia
        TextView Ubicacion=(TextView)convertView.findViewById(R.id.txtUbicacion);
        String distanciaBruta,distanciaMostrar,distanciaMostrarKm;
        double distanciaM, distanciaKm;

        distanciaM=Hatchs1.getDistancia();
        distanciaKm=distanciaM*0.001;

        distanciaMostrarKm=Double.toString(distanciaKm);

        distanciaBruta=Hatchs1.getDistancia().toString();
        distanciaMostrar=distanciaBruta.substring(0,distanciaBruta.indexOf("."));

        if (distanciaMostrar.length()<=3){
            Ubicacion.setText(distanciaMostrar+ " mts");
        }else{
            Ubicacion.setText(distanciaMostrarKm.substring(0,distanciaMostrarKm.indexOf("."))+ " kms");
        }
        //endregion

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

        return convertView;

    }

}
