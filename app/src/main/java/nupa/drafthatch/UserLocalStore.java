package nupa.drafthatch;

import android.content.Context;
import android.content.SharedPreferences;

/**
 CLASE PARA STOREAR LA INFORMACION DEL USUARIO EN UN FILE (SHAREDPREFERENCES)   OBTENER EL USUARIO LOGUEADO Y SETEAR EL USUARIO LOGUEADO
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase; //para guardar en la base de datos local

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
      }

    public void storeUserData ( User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();

        spEditor.putString("name", user.name);
        spEditor.putInt("age", user.age);
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.putInt("User_Id", user.User_Id);

        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDatabase.getString("name", "");
        int age = userLocalDatabase.getInt("age", -1);
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");
        int User_Id=userLocalDatabase.getInt("User_Id",0);

        User storedUser =  new User(name, age, username, password);
        storedUser.User_Id=User_Id;
        return storedUser;

    }

    public void  setUserLoggedIn (boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
            }

    public void cleanUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == true) {
            return true;
        } else {
            return false;
        }
    }




}

