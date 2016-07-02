package example.locations;

import example.locations.data.LocationService;
import example.locations.export.LocationsToFileExporter;
import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private Application application = new TestableApplication();
    private LocationsToFileExporter locationExporter = mock(LocationsToFileExporter.class);
    private LocationService locationService = mock(LocationService.class);

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

    @Test
    public void testRun_noArgs() throws Exception {
        String[] args = {};
        application.run(args);
        assertEquals("Usage: java -jar <application jar file> \"city name\"\n", outContent.toString());
        verify(locationService, never()).getLocations(any(String.class));
        verify(locationExporter, never()).export(any(List.class), any(String.class));
    }

    @Test
    public void testRun_zeroLengthArg() throws Exception {
        String[] args = {""};
        application.run(args);
        assertEquals("Invalid city name\n", outContent.toString());
        verify(locationService, never()).getLocations(any(String.class));
        verify(locationExporter, never()).export(any(List.class), any(String.class));
    }

    @Test
    public void testRun_invalidRequest() throws Exception {
        Call call = mock(Call.class);
        String[] args = {"city"};
        when(call.execute()).thenReturn(Response.error(500, new RealResponseBody(new Headers.Builder().build(), new Buffer().readFrom(new ByteArrayInputStream("Error".getBytes(StandardCharsets.UTF_8))))));
        when(locationService.getLocations("city")).thenReturn(call);
        application.run(args);
        verify(locationService, times(1)).getLocations("city");
        assertEquals("Error occurred: Couldn't fetch data from endpoint. Please check logs for more details\n", outContent.toString());
        verify(locationExporter, never()).export(any(List.class), any(String.class));
    }

    @Test
    public void testRun_normal() throws Exception {
        Call call = mock(Call.class);
        String[] args = {"city"};
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(1L, "name", "type", 1.0, 2.0));
        locations.add(new Location(2L, "name2", "typ2e", 3.0, 4.0));
        when(call.execute()).thenReturn(Response.success(locations));
        when(locationService.getLocations("city")).thenReturn(call);
        application.run(args);
        verify(locationService, times(1)).getLocations("city");
        verify(locationExporter, times(1)).export(locations, "locations");
        assertEquals("Exported 2 locations\n", outContent.toString());
    }

    @Test
    public void testRun_zeroLocations() throws Exception {
        Call call = mock(Call.class);
        String[] args = {"city"};
        List<Location> locations = new ArrayList<>();
        when(call.execute()).thenReturn(Response.success(locations));
        when(locationService.getLocations("city")).thenReturn(call);
        application.run(args);
        verify(locationService, times(1)).getLocations("city");
        verify(locationExporter, times(0)).export(locations, "locations");
        assertEquals("No locations found\n", outContent.toString());
    }

    @Test
    public void testRun_fileExists_doNotOverwrite() throws Exception {
        Call call = mock(Call.class);
        String[] args = {"city"};
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(1L, "name", "type", 1.0, 2.0));
        locations.add(new Location(2L, "name2", "typ2e", 3.0, 4.0));
        when(call.execute()).thenReturn(Response.success(locations));
        when(locationService.getLocations("city")).thenReturn(call);
        doThrow(new FileAlreadyExistsException("")).when(locationExporter).export(locations, "locations");

        String input = "n\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        application.run(args);
        verify(locationService, times(1)).getLocations("city");
        verify(locationExporter, times(1)).export(locations, "locations");
        assertEquals("File already exists, overwrite? Y/N ", outContent.toString());
    }

    @Test
    public void testRun_fileExists_doOverwrite() throws Exception {
        Call call = mock(Call.class);
        String[] args = {"city"};
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(1L, "name", "type", 1.0, 2.0));
        locations.add(new Location(2L, "name2", "typ2e", 3.0, 4.0));
        when(call.execute()).thenReturn(Response.success(locations));
        when(locationService.getLocations("city")).thenReturn(call);
        doThrow(new FileAlreadyExistsException("")).when(locationExporter).export(locations, "locations");

        String input = "y\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        application.run(args);
        verify(locationService, times(1)).getLocations("city");
        verify(locationExporter, times(1)).export(locations, "locations");
        verify(locationExporter, times(1)).export(locations, "locations", true);
        assertEquals("File already exists, overwrite? Y/N Exported 2 locations\n", outContent.toString());
    }

    @Test
    public void testRun_exception() throws Exception {
        when(locationService.getLocations("city")).thenThrow(new RuntimeException("Error"));
        String[] args = {"city"};
        application.run(args);
        assertEquals("Error occurred: Error. Please check logs for more details.\n", outContent.toString());
        verify(locationExporter, never()).export(any(List.class), any(String.class));
    }

    private class TestableApplication extends Application {

        @Override
        protected LocationsToFileExporter getLocationExporter() {
            return locationExporter;
        }

        @Override
        protected LocationService getLocationService() {
            return locationService;
        }
    }

}