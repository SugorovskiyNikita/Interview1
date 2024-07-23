package ru.learn.util;

import ru.learn.entity.ServiceDiscovery;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class Validation {

    public static void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("Name validation failed: name cannot be null or empty");
        }
    }

    public static void validateUri(URI uri) {
        if (uri == null || !uri.isAbsolute() || uri.toString().isEmpty()) {
            throw new ValidationException("URI validation failed: URI cannot be null, must be absolute, and cannot be empty");
        }
    }

    public static void checkDuplicateUri(Map<String, Set<ServiceDiscovery>> memoryDb, URI uri) {
        if (memoryDb.values().stream()
                .flatMap(Set::stream)
                .anyMatch(service -> service.getUri().equals(uri))) {
            throw new ValidationException(String.format("URI %s is already in use", uri));
        }
    }

    public static void validateUriLimit(Set<ServiceDiscovery> services, int maxUris) {
        if (services.size() >= maxUris) {
            throw new ValidationException(String.format("Cannot add more than %d URIs", maxUris));
        }
    }
}
