package example.locations.export;

import example.locations.Location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public abstract class LocationsToFileExporter {

    public abstract String getExtension();

    protected File createFile(String fileName) throws IOException {
        String fullFileName = buildFullFileName(fileName, getExtension());
        File file = new File(fullFileName);
        if (file.exists()) {
            throw new FileAlreadyExistsException("File " + fullFileName + " already exists");
        }
        if (!file.createNewFile()) {
            throw new IOException("Can't create file");
        }
        return file;
    }

    protected String buildFullFileName(String fileName, String extension) {
        return fileName + "." + extension;
    }

    protected abstract void writeLocations(List<Location> locations, OutputStream os) throws IOException;
    
    public void export(List<Location> locations, String fileName) throws IOException {
        File file = createFile(fileName);
        try(FileOutputStream fos = new FileOutputStream(file)) {
            writeLocations(locations, fos);
        }
    }

    public void export(List<Location> locations, String fileName, boolean overwriteFile) throws IOException {
        File file = new File(buildFullFileName(fileName, getExtension()));
        if (overwriteFile && file.exists()) {
            if (!file.delete()) {
                throw new IOException("Can't overwrite file.");
            }
        }
        export(locations, fileName);
    }

}
