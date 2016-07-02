package example.locations;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {

    @SerializedName("_id")
    private Long id;
    private String name;
    private String type;
    @SerializedName("geo_position")
    private GeoPosition geoPosition;

    public Location(Long id, String name, String type, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.geoPosition = new GeoPosition(latitude, longitude);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (type != null ? !type.equals(location.type) : location.type != null) return false;
        return !(geoPosition != null ? !geoPosition.equals(location.geoPosition) : location.geoPosition != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (geoPosition != null ? geoPosition.hashCode() : 0);
        return result;
    }

}
