package ru.learn.db.impl;

import ru.learn.db.interfaces.ServiceRepository;
import ru.learn.entity.ServiceDiscovery;


import java.net.URI;
import java.util.*;

import static ru.learn.util.Validation.*;

public class ServiceImpl implements ServiceRepository {

    private final Map<String, Set<ServiceDiscovery>> memoryDb;
    private final Integer maxUrisPerName;

    public ServiceImpl(Map<String, Set<ServiceDiscovery>> memoryDb, int maxUrisPerName) {
        this.memoryDb = memoryDb;
        this.maxUrisPerName = maxUrisPerName;
    }

    public void create(String name, URI uri) {
        validateName(name);
        validateUri(uri);
        checkDuplicateUri(memoryDb, uri);

        Set<ServiceDiscovery> services = memoryDb.computeIfAbsent(name, k -> new HashSet<>());
        validateUriLimit(services, maxUrisPerName);

        memoryDb.computeIfAbsent(name, k -> new HashSet<>()).add(new ServiceDiscovery(name, uri));
    }

    public URI getServiceUri(String name) {
        validateName(name);

        return Optional.ofNullable(memoryDb.get(name))
                .flatMap(services -> services.stream().findAny())
                .map(ServiceDiscovery::getUri)
                .orElseThrow(() -> new RuntimeException(String.format("URI with this name not found, %s", name)));
    }
}
