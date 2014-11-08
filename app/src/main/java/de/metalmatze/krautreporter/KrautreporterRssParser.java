package de.metalmatze.krautreporter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    if (tagName.equalsIgnoreCase("item"))
                    {
                        this.articles.add(this.article);
                    }
                    else if (tagName.equalsIgnoreCase("guid"))
                    {
                        this.article.setUuid(this.text);
                    }
                    else if (tagName.equalsIgnoreCase("title"))
                    {
                        this.article.setTitle(this.text.trim());
                    }
                    else if (tagName.equalsIgnoreCase("pubDate"))
                    {
                        this.article.setDate(this.parseDate(this.text));
                    }
                    else if (tagName.equalsIgnoreCase("link"))
                    {
                        this.article.setLink(new URL(this.text));
                    }
                    else if (tagName.equalsIgnoreCase("description"))
                    {
                        String content = this.parseContent(this.text);
                        this.article.setContent(content);
                    }
                    break;
                default:
                    break;
            }

            eventType = this.parser.next();
        }

        return this.articles;
    }

    private Date parseDate(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

        Date date = null;
        try {
            date = dateFormat.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private String parseContent(String text) {

        this.parseTeaserImage(text);

        return text;
    }

    private void parseTeaserImage(String text) {
        Pattern pattern = Pattern.compile("(<img src=')(.*(teaser_image).*)('\\/>)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find())
        {
            try {
                this.article.setTeaserImage(new URL("http://krautreporter.de" + matcher.group(2)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
