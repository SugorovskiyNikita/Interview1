import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.learn.db.impl.ServiceImpl;


import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceImplTest {

    private static final URI RAMBLER_URL = URI.create("https://www.rambler.ru/");
    private static final URI RAMBLER_SECOND_URL = URI.create("https://www.rambler.com/");
    private static final String RAMBLER = "rambler";


    private  Map<String, Set<URI>> memoryDb;
    private ServiceImpl service;

    @BeforeEach
    public void init() {
        memoryDb = new HashMap<>();
        service = new ServiceImpl(memoryDb);
    }

    @Test
    void shouldCreateNewServiceEntityWithValidData() {
        service.create(RAMBLER, RAMBLER_URL);

        assertTrue(memoryDb.containsKey(RAMBLER));
        assertTrue(memoryDb.get(RAMBLER).contains(RAMBLER_URL));
    }

    @Test
    public void shouldCreateServiceEntityHasWithSameName() {
        memoryDb.put(RAMBLER, new HashSet<>(Set.of(RAMBLER_URL)));

        service.create(RAMBLER, RAMBLER_SECOND_URL);

        assertTrue(memoryDb.containsKey(RAMBLER));
        assertTrue(memoryDb.get(RAMBLER).contains(RAMBLER_URL));
        assertTrue(memoryDb.get(RAMBLER).contains(RAMBLER_SECOND_URL));
    }

    @Test
    public void shouldCreateServiceEntityWithNameNull() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null, RAMBLER_URL),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Service validation failed"));
    }

    @Test
    public void shouldCreateServiceEntityWithEmptyName() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.create("", RAMBLER_URL),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Service validation failed"));
    }

    @Test
    public void  shouldCreateServiceEntityWithUriNull() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(RAMBLER, null),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Service validation failed"));
    }

    @Test
    public void shouldCreateServiceWithNonAbsoluteUri() {
        URI notAbsoluteIri = URI.create("/relative/path");

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(RAMBLER, notAbsoluteIri),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Service validation failed"));
    }

    @Test
    public void shouldCreateServiceWithEmptyUri() {
        URI notAbsoluteIri = URI.create("");

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(RAMBLER, notAbsoluteIri),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Service validation failed"));
    }

    @Test
    public void shouldGetServiceUriWithValidName() {
        memoryDb.put(RAMBLER, (Set.of(RAMBLER_URL)));

        URI result = service.getServiceUri(RAMBLER);

        assertEquals(RAMBLER_URL, result);
    }

    @Test
    public void shouldCreateServiceWithUriLimitExceeded() {
        for (int i = 0; i < 100; i++) {
            memoryDb.computeIfAbsent(RAMBLER, k -> new HashSet<>())
                    .add(URI.create("http://example.com/" + i));
        }

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> service.create(RAMBLER, RAMBLER_URL),
                "Expected create() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Cannot add more than 100 URIs for name: rambler"));
    }

    @Test
    public void shouldGetServiceUriWithNullName() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.getServiceUri(null),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    public void shouldGetServiceUriWithEmptyName() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.getServiceUri(""),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Name validation failed"));
    }

    @Test
    public void shouldGetServiceUriWithNonExistentName() {
        String name = "nonExistentName";

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.getServiceUri(name),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Uri with this name not found"));
    }

    @Test
    public void shouldGetServiceUriWithEmptyUriSet() {
        memoryDb.put(RAMBLER, new HashSet<>());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.getServiceUri(RAMBLER),
                "Expected getServiceUri() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Uri with this name not found"));
    }
}
