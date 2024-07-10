package ru.learn.db.impl;

import ru.learn.db.interfaces.ServiceRepository;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServiceImpl implements ServiceRepository {

    private final Map<String, Set<URI>> memoryDb;
    private static final int MAX_URIS_PER_NAME = 100;

    public ServiceImpl(Map<String, Set<URI>> memoryDb) {
        this.memoryDb = memoryDb;
    }

    public void create(String name, URI uri) {
        if (isValidName(name) && isValidUri(uri)) {
            memoryDb.compute(name, (k, uris) -> {
                if (uris == null) {
                    uris = new HashSet<>();
                }
                if (uris.size() >= MAX_URIS_PER_NAME) {
                    throw new IllegalStateException(String.format("Cannot add more than %d URIs for name: %s", MAX_URIS_PER_NAME, name));
                }
                uris.add(uri);
                return uris;
            });
        } else {
            throw new IllegalArgumentException(String.format("Service validation failed, %s, %s", name, uri));
        }
    }

    public URI getServiceUri(String name) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException(String.format("Name validation failed, %s", name));
        }

        return Optional.ofNullable(memoryDb.get(name))
                .flatMap(service -> service.stream().findAny())
                .orElseThrow(() -> new RuntimeException(String.format("Uri with this name not found, %s", name)));
    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidUri(URI uri) {
        return uri != null && uri.isAbsolute() && !uri.toString().isEmpty();
    }
}
