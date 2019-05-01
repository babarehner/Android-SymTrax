package edu.babarehner.android.symtrax;

import java.util.ArrayList;

public class Emotions {

    private static Emotions eClass = null;
    private String s;  // emotions should not be mutable
    public boolean b = false;
    public ArrayList<Emotions> emotions = null;


    // provate constructor restricted to this class itself
    private Emotions(){

        emotions.add(eClass.addItemToArrayList("None", false));
        emotions.add(eClass.addItemToArrayList("Anger(grouchy)", false));
        emotions.add(eClass.addItemToArrayList("Disgust", false));
        emotions.add(eClass.addItemToArrayList("Envy", false));
        emotions.add(eClass.addItemToArrayList("Fear (anxiety)", false));
        emotions.add(eClass.addItemToArrayList("Happiness", false));
        emotions.add(eClass.addItemToArrayList("Jealousy", false));
        emotions.add(eClass.addItemToArrayList("Happiness", false));
        emotions.add(eClass.addItemToArrayList("Love", false));
        emotions.add(eClass.addItemToArrayList("Sadness", false));
        emotions.add(eClass.addItemToArrayList("Shame", false));
        emotions.add(eClass.addItemToArrayList("Guilt (sorry)", false));

    }

    public static Emotions getInstance(){

        if (eClass == null){
            eClass = new Emotions();
        }
        return eClass;
    }

    public Emotions addItemToArrayList(String s, boolean b){
        this.s = s;
        this.b = b;
        return this;
    }


}

/***
// Java program implementing Singleton class
// with getInstance() method
class Singleton
{
    // static variable single_instance of type Singleton
    private static Singleton single_instance = null;

    // variable of type String
    public String s;

    // private constructor restricted to this class itself
    private Singleton()
    {
        s = "Hello I am a string part of Singleton class";
    }

    // static method to create instance of Singleton class
    public static Singleton getInstance()
    {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }
}

// Driver Class
class Main
{
    public static void main(String args[])
    {
        // instantiating Singleton class with variable x
        Singleton x = Singleton.getInstance();

        // instantiating Singleton class with variable y
        Singleton y = Singleton.getInstance();

        // instantiating Singleton class with variable z
        Singleton z = Singleton.getInstance();

        // changing variable of instance x
        x.s = (x.s).toUpperCase();

        System.out.println("String from x is " + x.s);
        System.out.println("String from y is " + y.s);
        System.out.println("String from z is " + z.s);
        System.out.println("\n");

        // changing variable of instance z
        z.s = (z.s).toLowerCase();

        System.out.println("String from x is " + x.s);
        System.out.println("String from y is " + y.s);
        System.out.println("String from z is " + z.s);
    }
}
***/