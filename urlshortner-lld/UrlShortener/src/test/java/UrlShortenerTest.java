import org.example.controller.UrlShortenerController;
import org.example.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlShortenerTest {
    private static UrlShortenerService urlShortenerService;
    private static UrlShortenerController controller;

    @BeforeAll
    static void setUp() {
        urlShortenerService = Mockito.mock(UrlShortenerService.class);
        controller = new UrlShortenerController(urlShortenerService);
    }

    @Test
    void shouldReturnShortCodeForGivenLongUrl() {
        String longUrl = "https://five9.com";
        String shortCode = "AAAAAA";

        Mockito.when(urlShortenerService.createShortUrl(longUrl))
                .thenReturn(shortCode);

        String result = controller.shortenUrl(longUrl);

        assertEquals(shortCode, result);
        Mockito.verify(urlShortenerService)
                .createShortUrl(longUrl);
    }

    @Test
    void shouldReturnLongUrlForGivenShortCode() {
        String shortCode = "AAAAAA";
        String longUrl = "https://google.com";

        Mockito.when(urlShortenerService.getLongUrl(shortCode))
                .thenReturn(longUrl);
        String result = controller.fetchLongUrl(shortCode);
        assertEquals(longUrl, result);

        Mockito.verify(urlShortenerService)
                .getLongUrl(shortCode);
    }
}
