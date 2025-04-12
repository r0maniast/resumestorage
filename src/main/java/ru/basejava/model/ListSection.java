package ru.basejava.model;

import java.util.List;
import java.util.Objects;

public class ListSection extends Section{
    private final List<Section> items;

    public ListSection(List<Section> items) {
        Objects.requireNonNull(items,"items must not be null");
        this.items = items;
    }

    public List<Section> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListSection that = (ListSection) o;

        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ListSection{" +
                "items=" + items +
                '}';
    }
}
