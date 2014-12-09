package de.metalmatze.krautreporter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.metalmatze.krautreporter.models.ArticleModel;

public class KrautreporterRssParser {

    private ArrayList<ArticleModel> articles;
    private ArticleModel article;
    private String text;
    protected XmlPullParser parser;

    public KrautreporterRssParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.parser = factory.newPullParser();
        this.articles = new ArrayList<>();
        this.article = new ArticleModel();
    }

    public ArrayList<ArticleModel> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        this.parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        this.parser.setInput(inputStream, "UTF-8");

        int eventType = this.parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            String tagName = this.parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase("item")) {
                        this.article = new ArticleModel();
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
                        this.article.uuid = this.text;
                    }
                    else if (tagName.equalsIgnoreCase("title"))
                    {
                        this.article.title = this.text;
                    }
                    else if (tagName.equalsIgnoreCase("pubDate"))
                    {
                        this.article.date = this.parseDate(this.text);
                    }
                    else if (tagName.equalsIgnoreCase("link"))
                    {
                        this.article.link = this.text;
                    }
                    else if (tagName.equalsIgnoreCase("description"))
                    {
                        String content = this.parseContent(this.text);
                        this.article.content = content;
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

        text = this.parseTeaserImage(text);
        text = this.parseExcerpt(text);

        return text;
    }

    private String parseTeaserImage(String text) {
        Pattern pattern = Pattern.compile("(<img src=')(.*(teaser_image).*)('\\/>)(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find())
        {
            this.article.image = "https://krautreporter.de" + matcher.group(2);

            return matcher.group(5);
        }

        return text;
    }

    private String parseExcerpt(String text) {
        Pattern pattern = Pattern.compile("(<p><\\/p>)*(<p>(.+?)<\\/p>)(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find())
        {
            this.article.excerpt = matcher.group(3);

            return matcher.group(4);
        }

        return text;
    }

}
