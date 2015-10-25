package de.metalmatze.krautreporter.api;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.metalmatze.krautreporter.R;
import io.realm.RealmObject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class Api {

    protected final Context context;
    private static ApiInterface singleton;

    public ApiInterface request() {
        if (singleton == null) {
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            singleton = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.url_api))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(ApiInterface.class);
        }

        return singleton;
    }

    public static Api with(Context context) {
        return new Api(context);
    }

    public Api(Context context) {
        this.context = context;
    }
}
