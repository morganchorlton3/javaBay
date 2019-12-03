package javaBay;

import net.jini.core.entry.Entry;

import java.io.File;

public class Lot implements Entry {
    // Variables
    private static Lot instance;
    public Integer lotNumber, userID, Status;
    public String lotName, lotDescription, userName;
    public Double BINprice, currentAprice, startAprice;
    public File lotImage;

    // No arg contructor
    public Lot (){
    }


    public Lot (int lotNo){
        lotNumber = lotNo;
    }

    public Lot (int lotNo, int user){
        lotNumber = lotNo;
        userID = user;
    }

    public Lot (String name){
        lotName = name;
    }

    // Arg constructor
    public Lot (int job, String name, String description, int userId, String nameUser, Double priceBIN, Double priceA, File image){
        lotNumber = job;
        lotName = name;
        lotDescription = description;
        userID = userId;
        userName = nameUser;
        BINprice = priceBIN;
        currentAprice = priceA;
        startAprice = priceA;
        lotImage = image;
        Status = 0;
    }

    // lot template for id
    /*public Lot (int ID){
        userID = ID;
    }*/

    public void nextlot(){
        lotNumber++;
    }

    public static Lot getInstance() {
        return instance;
    }

    public static void emptyInstance(){
        instance = null;
    }

    public static Lot getInstace(int job, String name, String description, int userId, String nameUser, Double priceBIN, Double priceA, File image) {
        if(instance == null) {
            instance = new Lot(job, name, description,userId,nameUser, priceBIN, priceA, image);
        }
        return instance;
    }

    @Override
    public String toString() {
        return "Lot{" +
                "lotNumber=" + lotNumber +
                ", userID=" + userID +
                ", Status=" + Status +
                ", lotName='" + lotName + '\'' +
                ", lotDescription='" + lotDescription + '\'' +
                ", userName='" + userName + '\'' +
                ", BINprice=" + BINprice +
                ", currentAprice=" + currentAprice +
                ", startAprice=" + startAprice +
                '}';
    }
}
