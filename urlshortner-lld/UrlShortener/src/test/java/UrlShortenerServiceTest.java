import org.example.controller.UrlShortenerController;
import org.example.repository.UrlRepository;
import org.example.service.UrlShortenerService;
import org.example.service.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlShortenerServiceTest {
    private static UrlShortenerService urlShortenerService;
    private static UrlRepository urlRepository;
    private static RandomGenerator randomGenerator;

    @BeforeAll
    static void setUp() {
        urlRepository = Mockito.mock(UrlRepository.class);
        randomGenerator = Mockito.mock(RandomGenerator.class);
        urlShortenerService = new UrlShortenerServiceImpl(urlRepository, randomGenerator);
    }

    @Test
    void createShortUrl() {
        String longUrl = "https://five9.com";
        String shortCode = "abcdef";
        Mockito.when(randomGenerator.nextInt(62)).thenReturn(10,11,12,13,14,15);
        String result = urlShortenerService.createShortUrl(longUrl);
        assertEquals(shortCode, result);
        Mockito.verify(randomGenerator, Mockito.times(6))
                .nextInt(62);
    }

//    @Test
//    void shouldReturnLongUrlForGivenShortCode() {
//        String shortCode = "AAAAAA";
//        String longUrl = "https://google.com";
//
//        Mockito.when(urlShortenerService.getLongUrl(shortCode))
//                .thenReturn(longUrl);
//        String result = controller.fetchLongUrl(shortCode);
//        assertEquals(longUrl, result);
//
//        Mockito.verify(urlShortenerService)
//                .getLongUrl(shortCode);
//    }
}
