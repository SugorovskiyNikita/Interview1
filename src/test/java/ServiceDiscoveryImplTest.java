import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.learn.db.impl.ServiceImpl;
import ru.learn.entity.ServiceDiscovery;
import ru.learn.util.ValidationException;


import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceDiscoveryImplTest {

    private static final URI RAMBLER_URL = URI.create("https://www.rambler.ru/");
    private static final URI RAMBLER_SECOND_URL = URI.create("https://www.rambler.com/");
    private static final String RAMBLER = "rambler";
    private static final Integer MAX_URIS = 100;

    private static final ServiceDiscovery FIRST_EXPECTED_SERVICE_DISCOVERY = new ServiceDiscovery(RAMBLER, RAMBLER_URL);
    private static final ServiceDiscovery SECOND_EXPECTED_SERVICE_DISCOVERY = new ServiceDiscovery(RAMBLER, RAMBLER_SECOND_URL);

    private Map<String, Set<ServiceDiscovery>> memoryDb;
    private ServiceImpl service;

    @BeforeEach
    public void init() {
        memoryDb = new HashMap<>();
        service = new ServiceImpl(memoryDb, MAX_URIS);
    }

    @Test
    void shouldCreateNewServiceEntityWithValidData() {
        service.create(RAMBLER, RAMBLER_URL);

        assertTrue(memoryDb.containsKey(RAMBLER));
        assertTrue(memoryDb.get(RAMBLER).contains(FIRST_EXPECTED_SERVICE_DISCOVERY));
    }

    @Test
    void shouldCreateServiceEntityHasWithSameName() {
        Set<ServiceDiscovery> initialServices = new HashSet<>(Set.of(FIRST_EXPECTED_SERVICE_DISCOVERY));
        memoryDb.put(RAMBLER, initialServices);

        service.create(RAMBLER, RAMBLER_SECOND_URL);

        assertTrue(memoryDb.containsKey(RAMBLER));
        assertTrue(memoryDb.get(RAMBLER).contains(FIRST_EXPECTED_SERVICE_DISCOVERY));
        assertTrue(memoryDb.get(RAMBLER).contains(SECOND_EXPECTED_SERVICE_DISCOVERY));
    }

    @Test
    void shouldThrowExceptionWithNullName() {
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create(null, RAMBLER_URL),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    void shouldThrowExceptionWithEmptyName() {
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create("", RAMBLER_URL),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    void shouldThrowExceptionWithNullUri() {
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create(RAMBLER, null),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("URI validation failed"));
    }

    @Test
    void shouldThrowExceptionWithNonAbsoluteUri() {
        URI notAbsoluteUri = URI.create("/relative/path");

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create(RAMBLER, notAbsoluteUri),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("URI validation failed"));
    }

    @Test
    void shouldThrowExceptionWithEmptyUri() {
        URI emptyUri = URI.create("");

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create(RAMBLER, emptyUri),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("URI validation failed"));
    }

    @Test
    void shouldThrowExceptionWhenUriLimitExceeded() {
        for (int i = 0; i < MAX_URIS; i++) {
            service.create(RAMBLER, URI.create("http://example.com/" + i));
        }

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.create(RAMBLER, URI.create("http://example.com/overflow")),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Cannot add more than 100 URIs"));
    }

    @Test
    void shouldGetServiceUriWithValidName() {
        Set<ServiceDiscovery> services = new HashSet<>(Set.of(FIRST_EXPECTED_SERVICE_DISCOVERY));
        memoryDb.put(RAMBLER, services);

        URI result = service.getServiceUri(RAMBLER);

        assertTrue(services.contains(new ServiceDiscovery(RAMBLER, result)));
    }

    @Test
    void shouldThrowExceptionWithNullNameWhenGettingUri() {
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.getServiceUri(null),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    void shouldThrowExceptionWithEmptyNameWhenGettingUri() {
        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> service.getServiceUri(""),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    void shouldThrowExceptionWhenNameDoesNotExist() {
        String name = "nonExistentName";

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.getServiceUri(name),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("URI with this name not found"));
    }

    @Test
    void shouldThrowExceptionWhenUriSetIsEmpty() {
        memoryDb.put(RAMBLER, new HashSet<>());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.getServiceUri(RAMBLER),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("URI with this name not found"));
    }
}
