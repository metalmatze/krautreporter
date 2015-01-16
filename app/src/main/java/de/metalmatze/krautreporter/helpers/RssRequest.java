package de.metalmatze.krautreporter.helpers;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.metalmatze.krautreporter.KrautreporterRssParser;
import de.metalmatze.krautreporter.models.Article;

public class RssRequest extends Request<List<Article>> {

    private final Response.Listener<List<Article>> responseListener;

    public RssRequest(int method, String url, Response.Listener<List<Article>> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.responseListener = responseListener;
    }

    @Override
    protected Response<List<Article>> parseNetworkResponse(NetworkResponse response) {

        List<Article> articles;

        try {

            InputStream inputStream = new ByteArrayInputStream(response.data);
            KrautreporterRssParser parser = new KrautreporterRssParser();

            articles = parser.parse(inputStream);

        } catch (XmlPullParserException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }

        return Response.success(articles, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(List<Article> response) {
        this.responseListener.onResponse(response);
    }
}
