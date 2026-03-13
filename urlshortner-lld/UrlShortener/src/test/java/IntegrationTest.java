import org.example.controller.UrlShortenerController;
import org.example.repository.UrlRepository;
import org.example.repository.UrlRepositoryImpl;
import org.example.service.UrlShortenerService;
import org.example.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private UrlShortenerController controller;

    @BeforeEach
    void setUp() {
        UrlRepository repository = new UrlRepositoryImpl();
        RandomGenerator randomGenerator = new Random();
        UrlShortenerService service = new UrlShortenerServiceImpl(repository, randomGenerator);
        controller = new UrlShortenerController(service);
    }

    @Test
    void testSingleUrlCreation() {
        String longUrl = "https://five9.com";
        String shortCode = controller.shortenUrl(longUrl);
        System.out.println(shortCode);
        String fetchedLongUrl = controller.fetchLongUrl(shortCode);
        assertNotNull(shortCode);
        assertEquals(longUrl, fetchedLongUrl);
    }

    @Test
    void testMultiplUrlCreation() {
        String longUrl1 = "https://five9.com";
        String shortCode1 = controller.shortenUrl(longUrl1);

        String longUrl2 = "https://five99.com";
        String shortCode2 = controller.shortenUrl(longUrl2);

        assertNotNull(shortCode1);
        assertNotNull(shortCode2);
        assertNotEquals(shortCode1, shortCode2);
    }

    @Test
    void testSameUrlCreationTwice() {
        String longUrl1 = "https://five9.com";
        String shortCode1 = controller.shortenUrl(longUrl1);

        String longUrl2 = "https://five9.com";
        String shortCode2 = controller.shortenUrl(longUrl2);

        assertNotNull(shortCode1);
        assertNotNull(shortCode2);
        assertNotEquals(shortCode1, shortCode2);
    }
}
