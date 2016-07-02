package example.locations.export;

import example.locations.Location;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsvLocationExporterTest {

    private static File file;

    private LocationsToCsvExporter locationExporter = new LocationsToCsvExporter() {
        @Override
        protected File createFile(String fileName) throws IOException {
            file = File.createTempFile(buildFullFileName(fileName, getExtension()), "");
            file.deleteOnExit();
            return file;
        }
    };

    @After
    public void tearDown() throws Exception {
        if (file != null) file.delete();
    }

    @Test
    public void testExport() throws Exception {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(1L, "name", "type", 1.0, 2.0));
        locations.add(new Location(2L, "name2", "type2", 3.0, 4.0));
        locationExporter.export(locations, "locations");
        assertTrue(file.getName().startsWith("locations.csv"));
        assertEquals("1;name;type;1.0;2.0\n2;name2;type2;3.0;4.0\n", readFile(file));
    }

    @Test
    public void testBuildFullFileName() throws Exception {
        assertEquals("filename.ext", locationExporter.buildFullFileName("filename", "ext"));
    }

    @Test
    public void testGetExtension() throws Exception {
        assertEquals("csv", locationExporter.getExtension());
    }

    private String readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        StringBuilder builder = new StringBuilder();
        int ch;
        while((ch = fis.read()) != -1){
            builder.append((char)ch);
        }
        return builder.toString();
    }
}