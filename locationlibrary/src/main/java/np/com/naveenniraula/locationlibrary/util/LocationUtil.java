package np.com.naveenniraula.locationlibrary.util;

import android.location.Location;

import java.util.ArrayList;
import java.util.Random;

public class LocationUtil {

    /**
     * Generates a random point withing the provided radius of the provided location.
     *
     * @param radiusInMeters  area up to where the location must be generated.
     * @param currentLocation current point of reference for the radiusInMeters
     * @return new random location within the specified constraint.
     * @see Location
     */
    public static Location getLocationInLatLngRad(double radiusInMeters, Location currentLocation) {
        double x0 = currentLocation.getLongitude();
        double y0 = currentLocation.getLatitude();

        Random random = new Random();

        // Convert radius from meters to degrees.
        double radiusInDegrees = radiusInMeters / 111320f;

        // Get a random distance and a random angle.
        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        // Get the x and y delta values.
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Compensate the x value.
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLatitude;
        double foundLongitude;

        foundLatitude = y0 + y;
        foundLongitude = x0 + new_x;

        Location copy = new Location(currentLocation);
        copy.setLatitude(foundLatitude);
        copy.setLongitude(foundLongitude);
        return copy;
    }

    /**
     * Generates the provided amount of random locations within the provided range.
     *
     * @param amountToGenerate total number of locations to be generated.
     * @param radiusInMeters   area up to where the location must be generated.
     * @return ArrayList of new locations.
     * @see LocationUtil
     */
    public static ArrayList<Location> getLocations(int amountToGenerate, long radiusInMeters) {

        ArrayList<Location> locations = new ArrayList<>();

        Location location = new Location("InvalidProvider");
        location.setLongitude(80.30278584);
        location.setLatitude(27.69792155);

        while (amountToGenerate-- > 0) {
            // locations.add(getLocation(27.69792, 80.30278, 10000));
            locations.add(getLocationInLatLngRad(radiusInMeters, location));
        }

        return locations;
    }

}
