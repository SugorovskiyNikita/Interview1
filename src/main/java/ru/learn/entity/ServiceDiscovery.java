package ru.learn.entity;

import java.net.URI;

public class ServiceDiscovery {

    private String name;
    private URI uri;

    public ServiceDiscovery() {
    }

    public ServiceDiscovery(String name, URI uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceDiscovery that = (ServiceDiscovery) o;

        if (!name.equals(that.name)) return false;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + uri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServiceDiscovery{" +
                "name='" + name + '\'' +
                ", uri=" + uri +
                '}';
    }
}
