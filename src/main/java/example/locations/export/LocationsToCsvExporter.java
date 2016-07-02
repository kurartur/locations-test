package example.locations.export;

import example.locations.Location;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class LocationsToCsvExporter extends LocationsToFileExporter {

    private static final String DELIMITER = ";";
    private static final String EXTENSION = "csv";

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    protected void writeLocations(List<Location> locations, OutputStream os) throws IOException {
        for (Location location : locations) {
            StringBuilder sb = new StringBuilder();
            sb.append(location.getId());
            sb.append(DELIMITER);
            sb.append(location.getName());
            sb.append(DELIMITER);
            sb.append(location.getType());
            sb.append(DELIMITER);
            sb.append(location.getGeoPosition().getLatitude());
            sb.append(DELIMITER);
            sb.append(location.getGeoPosition().getLongitude());
            sb.append('\n');
            os.write(sb.toString().getBytes());
        }
    }
}
