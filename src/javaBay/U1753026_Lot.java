package javaBay;


import net.jini.core.entry.Entry;


public class U1753026_Lot implements Entry {
    // Variables
    private static U1753026_Lot instance;
    public Integer lotNumber, userID, Status;
    public String lotName, lotDescription, userName;
    public Double BINprice, currentAprice, startAprice;
    public byte[] lotImage;

    // No arg contructor
    public U1753026_Lot(){
    }


    public U1753026_Lot(int lotNo){
        lotNumber = lotNo;
    }

    public U1753026_Lot(int lotNo, int user){
        lotNumber = lotNo;
        userID = user;
    }

    public U1753026_Lot(String name){
        lotName = name;
    }

    // Arg constructor
    public U1753026_Lot(int job, String name, String description, int userId, String nameUser, Double priceBIN, Double priceA, byte[] image) {
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

    public static U1753026_Lot getInstance() {
        return instance;
    }

    public static void emptyInstance(){
        instance = null;
    }

    public static U1753026_Lot getInstace(int job, String name, String description, int userId, String nameUser, Double priceBIN, Double priceA, byte[] image) {
        if(instance == null) {
            instance = new U1753026_Lot(job, name, description,userId,nameUser, priceBIN, priceA, image);
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
