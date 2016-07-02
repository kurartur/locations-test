package example.locations;

import java.io.Serializable;

public class GeoPosition implements Serializable {

    private Double latitude;
    private Double longitude;

    public GeoPosition(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPosition that = (GeoPosition) o;

        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        return !(longitude != null ? !longitude.equals(that.longitude) : that.longitude != null);

    }

    @Override
    public int hashCode() {
        int result = latitude != null ? latitude.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }

}
