package ru.basejava.model;

import java.util.Objects;

public class TextSection extends Section{
    private final static long serialVersionUID = 1L;

    private final String content;

    public TextSection(String title) {
        Objects.requireNonNull(title, "title must not be null");
        this.content = title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextSection that = (TextSection) o;

        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "TextSection{" +
                "content='" + content + '\'' +
                '}';
    }
}
