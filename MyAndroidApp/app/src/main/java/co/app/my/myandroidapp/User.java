package co.app.my.myandroidapp;

public class User {

    String email;
    String name;
    String surname;

    public User() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public User(String email,String name, String surname){
        // This constructor is used for Saving Users from FireBase
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public String GetName(){
        return name;
    }

    public String GetSurname(){
        return surname;
    }

    public String GetEmail(){
        return email;
    }



}
