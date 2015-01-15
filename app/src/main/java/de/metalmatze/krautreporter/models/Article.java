package de.metalmatze.krautreporter.models;

import java.util.Date;

public interface Article {

    public String getUuid();
    public void setUuid(String uuid);

    public String getTitle();
    public void setTitle(String title);

    public Date getDate();
    public void setDate(Date date);

    public String getLink();
    public void setLink(String link);

    public String getImage();
    public void setImage(String image);

    public String getExcerpt();
    public void setExcerpt(String excerpt);

    public String getContent();
    public void setContent(String content);

}
