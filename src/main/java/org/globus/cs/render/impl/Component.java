package org.globus.cs.render.impl;

import java.io.Serializable;
import java.util.Map;

public class Component implements Serializable {

    public String title;
    public String author;
    public String description;
    public String version;
    public Content content;
    public String uri;
    public Map<String, Preload> preload;

    public Component() {
    }

    public Component(String title, String author, String description, String version, Content content) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.version = version;
        this.content = content;
    }    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        if (author != null ? !author.equals(component.author) : component.author != null) return false;
        if (content != null ? !content.equals(component.content) : component.content != null) return false;
        if (description != null ? !description.equals(component.description) : component.description != null)
            return false;
        if (preload != null ? !preload.equals(component.preload) : component.preload != null) return false;
        if (title != null ? !title.equals(component.title) : component.title != null) return false;
        return !(version != null ? !version.equals(component.version) : component.version != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (preload != null ? preload.hashCode() : 0);
        return result;
    }
}
