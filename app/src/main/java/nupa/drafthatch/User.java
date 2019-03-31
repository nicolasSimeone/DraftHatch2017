package nupa.drafthatch;

/**
 * Created by Mauro Medina on 27/02/2016.
 */
public class User {

    String name, username, password, registrationID;
    int age, User_Id;

    public User (String name, int age, String username, String password){
        this.name = name;
        this.age = age;
        this.username = username;
        this.password = password;
        this.User_Id=0;


    }

    public User ( String username, String password){
        this.name = "";
        this.age = -1;
        this.username = username;
        this.password = password;
        this.User_Id=0;
    }

}
