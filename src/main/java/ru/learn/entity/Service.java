package ru.learn.entity;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Service {

    private String name;
    private Set<URI> uris;

    public Service() {
    }

    public Service(String name, Set<URI> uris) {
        this.name = name;
        this.uris = new HashSet<>(uris);
    }

    public Set<URI> getUris() {
        return Set.copyOf(uris);
    }

    public void setUris(Set<URI> uris) {
        this.uris = new HashSet<>(uris);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(name, service.name) && Objects.equals(uris, service.uris);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uris);
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", uris=" + uris +
                '}';
    }
}
