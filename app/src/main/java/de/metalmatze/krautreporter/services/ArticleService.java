package de.metalmatze.krautreporter.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.activeandroid.query.Select;
import com.activeandroid.query.Sqlable;

import java.util.List;

import de.metalmatze.krautreporter.models.ArticleModel;

public class ArticleService extends Service {

    public List<ArticleModel> all()
    {
        new SqlExecuter().execute(new Select().from(ArticleModel.class));

        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class SqlExecuter extends AsyncTask<Sqlable, Void, Void> {

        @Override
        protected Void doInBackground(Sqlable... params) {
            return null;
        }
    }

}
