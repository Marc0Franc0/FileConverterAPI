package com.marco.service;

import com.marco.util.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    @InjectMocks
    private ImageService imgService;
    @Mock
    private ImageUtil imgUtil;
    // getWriteableFormats()
    @Test
    void testGetWriteableFormats_returnsLowercaseAndUniqueFormats() {
        Set<String> mockFormats = new HashSet<>(Arrays.asList("PNG", "JPEG", "Gif", "png"));
        when(imgUtil.getWriteableFormats()).thenReturn(mockFormats);

        Set<String> result = imgService.getWriteableFormats();

        Set<String> expectedFormats = new HashSet<>(Arrays.asList("png", "jpeg", "gif"));
        assertEquals(expectedFormats.size(), result.size(), "Result set should have the correct number of unique elements.");
        assertTrue(result.containsAll(expectedFormats), "Result set should contain all expected lowercase formats.");
        assertTrue(expectedFormats.containsAll(result), "Result set should not contain unexpected formats.");

        verify(imgUtil, times(1)).getWriteableFormats();
    }
    @Test
    public void testGetWriteableFormats_emptySet() {
        when(imgUtil.getWriteableFormats()).thenReturn(Collections.emptySet());

        Set<String> result = imgService.getWriteableFormats();

        assertTrue( result.isEmpty(),"Result should be an empty set when the mock returns an empty set.");
        verify(imgUtil, times(1)).getWriteableFormats();
    }

    @Test
    public void testGetWriteableFormats_nullValueFromMock() {
        when(imgUtil.getWriteableFormats()).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            imgService.getWriteableFormats();
        }, "NullPointerException should be thrown if getWriteableFormats returns null.");

        verify(imgUtil, times(1)).getWriteableFormats();
    }
    //getReadableFormats()
    @Test
    void testGetReadableFormats_returnsLowercaseAndUniqueFormats() {
        Set<String> mockFormats = new HashSet<>(Arrays.asList("PNG", "JPEG", "Gif", "png"));
        when(imgUtil.getReadableFormats()).thenReturn(mockFormats);

        Set<String> result = imgService.getReadableFormats();

        Set<String> expectedFormats = new HashSet<>(Arrays.asList("png", "jpeg", "gif"));
        assertEquals(expectedFormats.size(), result.size(), "Result set should have the correct number of unique elements.");
        assertTrue(result.containsAll(expectedFormats), "Result set should contain all expected lowercase formats.");
        assertTrue(expectedFormats.containsAll(result), "Result set should not contain unexpected formats.");

        verify(imgUtil, times(1)).getReadableFormats();
    }
    @Test
    public void testGetReadableFormats_emptySet() {
        when(imgUtil.getReadableFormats()).thenReturn(Collections.emptySet());

        Set<String> result = imgService.getReadableFormats();

        assertTrue(result.isEmpty(),"Result should be an empty set when the mock returns an empty set.");
        verify(imgUtil, times(1)).getReadableFormats();
    }

    @Test
    public void testGetReadableFormats_nullValueFromMock() {
        when(imgUtil.getReadableFormats()).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            imgService.getReadableFormats();
        }, "NullPointerException should be thrown if getReadableFormats returns null.");

        verify(imgUtil, times(1)).getReadableFormats();
    }
}
