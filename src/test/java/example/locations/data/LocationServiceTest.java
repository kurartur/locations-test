package example.locations.data;

import example.locations.Location;
import okhttp3.*;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class LocationServiceTest {

    private static final String NO_LOCATIONS = "[]";
    private static final String TWO_LOCATIONS = "[\n" +
            "\n" +
            " {\n" +
            "\n" +
            " \"_id\": 377078,\n" +
            " \"key\": null,\n" +
            " \"name\": \"Potsdam\",\n" +
            " \"fullName\": \"Potsdam, Germany\",\n" +
            " \"iata_airport_code\": null,\n" +
            " \"type\": \"location\",\n" +
            " \"country\": \"Germany\",\n" +
            "\n" +
            " \"geo_position\": {\n" +
            " \"latitude\": 52.39886,\n" +
            " \"longitude\": 13.06566\n" +
            " },\n" +
            " \"location_id\": 377078,\n" +
            " \"inEurope\": true,\n" +
            " \"countryCode\": \"DE\",\n" +
            " \"coreCountry\": true,\n" +
            " \"distance\": null\n" +
            " },\n" +
            "\n" +
            " {\n" +
            " \"_id\": 410978,\n" +
            " \"key\": null,\n" +
            " \"name\": \"Potsdam\",\n" +
            " \"fullName\": \"Potsdam, USA\",\n" +
            " \"iata_airport_code\": null,\n" +
            " \"type\": \"location\",\n" +
            " \"country\": \"USA\",\n" +
            "\n" +
            " \"geo_position\": {\n" +
            " \"latitude\": 44.66978,\n" +
            " \"longitude\": -74.98131\n" +
            " },\n" +
            "\n" +
            " \"location_id\": 410978,\n" +
            " \"inEurope\": false,\n" +
            " \"countryCode\": \"US\",\n" +
            " \"coreCountry\": false,\n" +
            " \"distance\": null\n" +
            " }\n" +
            " ]";

    @Test
    public void testGetLocations_empty() throws Exception {
        LocationService locationService = RestClient.createService(LocationService.class, createFakeClient(NO_LOCATIONS));
        Call<List<Location>> getLocationsCall = locationService.getLocations("city");
        assertEquals("http://api.goeuro.com/api/v2/position/suggest/en/city", getLocationsCall.request().url().toString());
        getLocationsCall.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(Call<List<Location>> call, retrofit2.Response<List<Location>> response) {
                assertTrue(response.isSuccessful());
                assertTrue(response.body().isEmpty());
            }

            @Override
            public void onFailure(Call<List<Location>> call, Throwable t) {
                fail();
            }
        });
    }

    @Test
    public void testGetLocations() throws Exception {
        LocationService locationService = RestClient.createService(LocationService.class, createFakeClient(TWO_LOCATIONS));
        Call<List<Location>> getLocationsCall = locationService.getLocations("city");
        assertEquals("http://api.goeuro.com/api/v2/position/suggest/en/city", getLocationsCall.request().url().toString());
        retrofit2.Response<List<Location>> response = getLocationsCall.execute();
        assertTrue(response.isSuccessful());
        assertFalse(response.body().isEmpty());
        Iterator<Location> locations = response.body().iterator();
        assertTrue(locations.hasNext());
        assertTrue(locations.next().equals(new Location(377078L, "Potsdam", "location", 52.39886, 13.06566)));
        assertTrue(locations.hasNext());
        assertTrue(locations.next().equals(new Location(410978L, "Potsdam", "location", 44.66978, -74.98131)));
        assertFalse(locations.hasNext());
    }

    private static class FakeResponseInterceptor implements Interceptor {

        private String responseString;

        public FakeResponseInterceptor(String responseString) {
            this.responseString = responseString;
        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            return new Response.Builder()
                    .code(200)
                    .message(responseString)
                    .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                    .build();
        }
    }

    private OkHttpClient createFakeClient(String fakeResponse) {
        return new OkHttpClient.Builder().addInterceptor(new FakeResponseInterceptor(fakeResponse)).build();
    }
}