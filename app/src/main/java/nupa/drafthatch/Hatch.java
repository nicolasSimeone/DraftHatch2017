package nupa.drafthatch;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by sebastianfaro on 27/11/15.
 */
public class Hatch implements ClusterItem {

    private String Id;
    private String Title;
    private String Body;
    private String Ubicacion_lat;
    private String Ubicacion_lon;
    private String Username;
    private String Direccion;
    private String Tipo;
    private int Id_Hatch;
    private int Participantes;
    private String FechaInicio;
    private String FechaFin;
    private Double Distancia;
    private String Fecha;
    private String Categoria;
    private String Estado;
    private String OwnerIDHatch;
    private String UserEmail;

    public Long getEpochStart() {
        return EpochStart;
    }

    public void setEpochStart(Long epochStart) {
        EpochStart = epochStart;
    }

    private Long EpochStart;

    public Long getEpochEnd() {
        return EpochEnd;
    }

    public void setEpochEnd(Long epochEnd) {
        EpochEnd = epochEnd;
    }

    private Long EpochEnd;

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }


    //region get set

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        FechaInicio = fechaInicio;
    }



    public String getFechaFin() {
        return FechaFin;
    }

    public void setFechaFin(String fechaFin) {
        FechaFin = fechaFin;
    }




    public int getParticipantes() {
        return Participantes;
    }

    public void setParticipantes(int participantes) {
        Participantes = participantes;
    }



    public int getId_Hatch() {
        return Id_Hatch;
    }

    public void setId_Hatch(int id_Hatch) {
        Id_Hatch = id_Hatch;
    }



    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }



    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }




    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }


    private int User_Id;


    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }



    public Double getDistancia() {
        return Distancia;
    }

    public void setDistancia(Double distancia) {
        Distancia = distancia;
    }



    public String getUbicacion_lon() {
        return Ubicacion_lon;
    }

    public void setUbicacion_lon(String ubicacion_lon) {
        Ubicacion_lon = ubicacion_lon;
    }


    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }




    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }



    public String getUbicacion_lat() {
        return Ubicacion_lat;
    }

    public void setUbicacion_lat(String ubicacion_lat) {
        Ubicacion_lat = ubicacion_lat;
    }



    public String getId() {
        return Id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }



    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getOwnerIDHatch() {
        return OwnerIDHatch;
    }

    public void setOwnerIDHatch(String oih) {
        OwnerIDHatch = oih;
    }

    //endregion

    public Hatch(String hid, String htitle, String hbody, String hlat, String hlng, String husername, String hdireccion,
                 String htipo, Integer hidhatch, Integer hparticipantes, String hfechainicio, String hFechafin ,
                 String hEstado, Double hdistancia, String hCategoria, String OwnerIDH, String HUserEmail, Long hEpochStart, Long hEpochEnd){
        Id=hid;
        Title=htitle;
        Body=hbody;
        Ubicacion_lat=hlat;
        Ubicacion_lon=hlng;
        Username=husername;
        Direccion=hdireccion;
        Tipo=htipo;
        Id_Hatch=hidhatch;
        Participantes=hparticipantes;
        FechaInicio=hfechainicio;
        FechaFin=hFechafin;
        Estado=hEstado;
        Distancia = hdistancia;
        Categoria = hCategoria;
        OwnerIDHatch=OwnerIDH;
        UserEmail = HUserEmail;
        EpochStart = hEpochStart;
        EpochEnd = hEpochEnd;
    }


    public Hatch(){}

    @Override
    public LatLng getPosition() {
        LatLng mPosition = new LatLng(Double.parseDouble(Ubicacion_lat), Double.parseDouble(Ubicacion_lon));
        return mPosition;
    }
}
