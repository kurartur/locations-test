package example.locations.data;

import example.locations.Location;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface LocationService {

    @GET("position/suggest/en/{cityName}")
    Call<List<Location>> getLocations(@Path("cityName") String cityName);

}
