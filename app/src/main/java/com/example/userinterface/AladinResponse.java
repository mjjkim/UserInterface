package com.example.userinterface;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response", strict = false)
public class AladinResponse {
    @ElementList(name = "item", inline = true, required = false)
    private List<BookItem> items;

    public List<BookItem> getItems() {
        return items;
    }
}

@Root(name = "item", strict = false)
class Item {
    @Element(name = "title", required = false)
    private String title;

    @Element(name = "author", required = false)
    private String author;

    @Element(name = "cover", required = false)
    private String cover; // 책 표지 이미지 URL

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCover() {
        return cover;
    }
}
