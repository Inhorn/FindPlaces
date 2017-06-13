package babiy.findplaces.model;


public class DataOfPlace {

    private String id;
    private String name;
    private String address;
    private String opening;
    private String rating;
    private double distance;
    private float locationLat;
    private float locationLng;

    public DataOfPlace () {

    }

    public void setId (String  id){
        this.id = id;
    }

    public void setLocationLat (float locationLat){
        this.locationLat = locationLat;
    }

    public void setLocationLng (float locationLng){
        this.locationLng = locationLng;
    }

    public void setName (String  name){
        this.name = name;
    }

    public void setAddress (String address){
        this.address = address;
    }

    public void setOpening (String opening){
        this.opening = opening;
    }

    public void setRating (String rating){
        this.rating = rating;
    }

    public void setDistance (double distance){
        this.distance = distance;
    }

    public String getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public String getAddress () {
        return address;
    }

    public String getOpening () {
        return opening;
    }

    public String getRating () {
        return rating;
    }

    public double getDistance () {
        return distance;
    }

    public float getLocationLat () {
        return locationLat;
    }

    public float getLocationLng () {
        return locationLng;
    }


}
