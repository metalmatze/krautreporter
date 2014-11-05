package de.metalmatze.krautreporter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import de.metalmatze.krautreporter.entities.Article;

public class KrautreporterRssParser {

    private ArrayList<Article> articles;
    private Article article;
    private String text;
    private static final String ns = null;
    protected XmlPullParser parser;

    public KrautreporterRssParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.parser = factory.newPullParser();
        this.articles = new ArrayList<Article>();
        this.article = new Article();
    }

    public ArrayList<Article> parse(BufferedInputStream bufferedInputStream) throws XmlPullParserException, IOException {
        this.parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        this.parser.setInput(bufferedInputStream, null);

        int eventType = this.parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            String tagName = this.parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("item")) {
                        this.article = new Article();
                    }
                    break;
                case XmlPullParser.TEXT:
                    this.text = this.parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase("item")) {
                        this.articles.add(this.article);
                    } else if (tagName.equalsIgnoreCase("guid")) {
                        this.article.setUuid(this.text);
                    } else if (tagName.equalsIgnoreCase("title")) {
                        if (this.text != null)
                            this.article.setTitle(this.text.trim());
                    }
                    else if (tagName.equalsIgnoreCase("pubDate")) {
                        try {
                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime(DateFormat.getDateTimeInstance().parse(this.text));
                            this.article.setDate(calendar);
                        } catch (ParseException e) {
                            this.article.setDate(new GregorianCalendar());
                        }
                    }
                    else if (tagName.equalsIgnoreCase("link")) {
                        URL link = new URL(this.text);
                        this.article.setLink(link);
                    }
                    else if (tagName.equalsIgnoreCase("description")) {
                        this.article.setContent(this.text);
                    }
                    break;
                default:
                    break;
            }

            eventType = this.parser.next();
        }

        return this.articles;
    }

}
