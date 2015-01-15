package de.metalmatze.krautreporter.models;

import java.util.Date;

public interface Article {

    Long getId();
    Long save();

    String getUuid();
    void setUuid(String uuid);

    String getTitle();
    void setTitle(String title);

    Date getDate();
    void setDate(Date date);

    String getLink();
    void setLink(String link);

    String getImage();
    void setImage(String image);

    String getExcerpt();
    void setExcerpt(String excerpt);

    String getContent();
    void setContent(String content);
}
