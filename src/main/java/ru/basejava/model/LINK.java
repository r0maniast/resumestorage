package ru.basejava.model;

import java.io.Serializable;
import java.util.Objects;

public class LINK implements Serializable {
    private final static long serialVersionUID = 1L;

    private final String name;
    private final String URL;

    public LINK(String name, String url) {
        Objects.requireNonNull(name, "fullName must not be null");
        this.name = name;
        this.URL = url;
    }

    public String getURL() {
        return URL;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LINK link = (LINK) o;

        if (!name.equals(link.name)) return false;
        return Objects.equals(URL, link.URL);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LINK{" +
                "name='" + name + '\'' +
                ", URL='" + URL + '\'' +
                '}';
    }
}
