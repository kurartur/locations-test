package example.locations.data;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static final String API_BASE = "http://api.goeuro.com/api/v2/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, httpClient.build());
    }

    public static <S> S createService(Class<S> serviceClass, OkHttpClient client) {
        Retrofit retrofit = builder
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }
}
