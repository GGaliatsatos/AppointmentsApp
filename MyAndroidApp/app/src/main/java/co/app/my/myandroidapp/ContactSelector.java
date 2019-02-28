package co.app.my.myandroidapp;

/**
 * Created by geros on 9/30/2016.
 */
public class ContactSelector {
    private String name;
    private boolean selected;

    public ContactSelector(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

}
