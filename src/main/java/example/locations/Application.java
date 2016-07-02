package example.locations;

import example.locations.data.LocationService;
import example.locations.data.RestClient;
import example.locations.export.LocationsToCsvExporter;
import example.locations.export.LocationsToFileExporter;
import org.apache.log4j.Logger;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static final String FILE_NAME = "locations";

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar <application jar file> \"city name\"");
            return;
        }
        String city = args[0].trim();
        if (city.length() == 0) {
            System.out.println("Invalid city name");
            return;
        }

        try {
            Response<List<Location>> response = getLocationService().getLocations(city).execute();
            if (!response.isSuccessful()) {
                logger.error(response.errorBody().string());
                System.out.println("Error occurred: Couldn't fetch data from endpoint. Please check logs for more details");
            } else {
                LocationsToFileExporter exporter = getLocationExporter();
                List<Location> locations = response.body();
                if (locations.size() == 0) {
                    System.out.println("No locations found");
                } else {
                    try {
                        exporter.export(locations, FILE_NAME);
                        System.out.println("Exported " + locations.size() + " locations");
                    } catch (FileAlreadyExistsException faes) {
                        askToOverwrite(exporter, locations);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage() + ". Please check logs for more details.");
            logger.error(e.getMessage(), e);
        }
    }

    private void askToOverwrite(LocationsToFileExporter exporter, List<Location> locations) throws IOException {
        System.out.print("File already exists, overwrite? Y/N ");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.next();
        if (response.equalsIgnoreCase("y")) {
            exporter.export(locations, FILE_NAME, true);
            System.out.println("Exported " + locations.size() + " locations");
        } else if (!response.equalsIgnoreCase("n")) {
          askToOverwrite(exporter, locations);
        }
    }

    protected LocationsToFileExporter getLocationExporter() {
        return new LocationsToCsvExporter();
    }

    protected LocationService getLocationService() {
        return RestClient.createService(LocationService.class);
    }

    public static void main(String[] args) {
        new Application().run(args);
    }

}
