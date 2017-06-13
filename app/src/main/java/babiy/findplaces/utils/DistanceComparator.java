package babiy.findplaces.utils;


import java.util.Comparator;


import babiy.findplaces.model.DataOfPlace;

public class DistanceComparator implements Comparator<DataOfPlace> {
    @Override
    public int compare(DataOfPlace o1, DataOfPlace o2) {
        double distance1 = o1.getDistance();
        double distance2 = o2.getDistance();
        if (distance1 <= distance2)
            return -1;
        else
            return 1;
    }
}