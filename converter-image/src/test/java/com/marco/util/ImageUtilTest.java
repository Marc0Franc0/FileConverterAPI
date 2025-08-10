package com.marco.util;

import com.marco.exception.ReadFileException;
import com.marco.exception.WriteFileException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class ImageUtilTest {

    @InjectMocks
    private ImageUtil imageUtil;

    @Mock
    private Set<String> mockReadableFormats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageUtil = new ImageUtil();
    }
    //validateReadableFormat***
    @Test
    void validateReadableFormat_validFormat_noExceptionThrown() {
        String readableFormat = "png";

        // se espera que no haya excepcion
        assertDoesNotThrow(() -> imageUtil.validateReadableFormat(readableFormat));
    }
    @Test
    void validateReadableFormat_unsupportedFormat_throwsReadFileException() {
        String unsupportedFormat = "xyz";

        // se espera excepcion ReadFileException con mensaje "Unsupported format for reading:formato"
        ReadFileException thrown = assertThrows(ReadFileException.class,
                () -> imageUtil.validateReadableFormat(unsupportedFormat));
        assertThrowsExactly(ReadFileException.class,() -> imageUtil.validateReadableFormat(unsupportedFormat));
        assertTrue(thrown.getMessage().contains("Unsupported format for reading: " + unsupportedFormat));
    }
    @Test
    void validateReadableFormat_nullFormat_throwsReadFileException() {
        // se espera excepcion ReadFileException con mensaje "Format cannot be null"
        ReadFileException thrown = assertThrows(ReadFileException.class,
                () -> imageUtil.validateReadableFormat(null));
        assertThrowsExactly(ReadFileException.class,() -> imageUtil.validateReadableFormat(null));
        assertEquals("Format cannot be null", thrown.getMessage());
    }

    @Test
    void validateReadableFormat_emptyReadableFormats_throwsReadFileException() throws IllegalAccessException, NoSuchFieldException {
        Field readableFormatsField = ImageUtil.class.getDeclaredField("readableFormats");
        readableFormatsField.setAccessible(true);

        ImageUtil testImageUtil = new ImageUtil();

        //se establece los formatos soportados como vacio
        readableFormatsField.set(testImageUtil, Collections.emptySet());

        ReadFileException thrown = assertThrows(ReadFileException.class,
                    () -> testImageUtil.validateReadableFormat("png"));

        assertEquals("No readable formats configured.", thrown.getMessage());


    }
    //validateWriteableFormat***
    @Test
    void validateWriteableFormat_validFormat_noExceptionThrown() {
        String readableFormat = "png";

        // se espera que no haya excepcion
        assertDoesNotThrow(() -> imageUtil.validateWriteableFormat(readableFormat));
    }
    @Test
    void validateWriteableFormat_unsupportedFormat_throwsWriteFileException() {
        String unsupportedFormat = "xyz";

        // se espera excepcion WriteFileException con mensaje "Unsupported format for writing:formato"
        WriteFileException thrown = assertThrows(WriteFileException.class,
                () -> imageUtil.validateWriteableFormat(unsupportedFormat));
        assertThrowsExactly(WriteFileException.class,() -> imageUtil.validateWriteableFormat(unsupportedFormat));
        assertTrue(thrown.getMessage().contains("Unsupported format for writing: " + unsupportedFormat));
    }
    @Test
    void validateWriteableFormat_nullFormat_throwsWriteFileException() {
        // se espera excepcion WriteFileException con mensaje "Format cannot be null"
        WriteFileException thrown = assertThrows(WriteFileException.class,
                () -> imageUtil.validateWriteableFormat(null));
        assertThrowsExactly(WriteFileException.class,() -> imageUtil.validateWriteableFormat(null));
        assertEquals("Format cannot be null", thrown.getMessage());
    }

    @Test
    void validateWriteableFormat_emptyReadableFormats_throwsWriteFileException() throws IllegalAccessException, NoSuchFieldException {
        Field writeableFormatsField = ImageUtil.class.getDeclaredField("writeableFormats");
        writeableFormatsField.setAccessible(true);

        ImageUtil testImageUtil = new ImageUtil();

        //se establece los formatos soportados como vacio
        writeableFormatsField.set(testImageUtil, Collections.emptySet());

        WriteFileException thrown = assertThrows(WriteFileException.class,
                () -> testImageUtil.validateWriteableFormat("png"));

        assertEquals("No writeable formats configured.", thrown.getMessage());


    }
    //readImage()**
    @Test
    void readImage_whenInputStreamIsValid_shouldReturnBufferedImage() throws IOException, ReadFileException {
        InputStream mockInputStream = mock(InputStream.class);

        BufferedImage expectedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(any(InputStream.class)))
                    .thenReturn(expectedImage);

            BufferedImage resultImage = imageUtil.readImage(mockInputStream);

            assertNotNull(resultImage, "The image must not be null.");
            assertEquals(expectedImage, resultImage, "The returned image must be the one expected from the mock.");
        }
    }

    @Test
    void readImage_whenFileIsInvalid_shouldThrowReadFileException() {
        InputStream mockInputStream = mock(InputStream.class);

        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(any(InputStream.class)))
                    .thenReturn(null);

            // Se verifica que se lanza la excepción correcta
            ReadFileException exception = assertThrows(ReadFileException.class, () -> {
                imageUtil.readImage(mockInputStream);
            });

            // se verifica el mensaje de la excepción
            assertEquals("Invalid file for image conversion", exception.getMessage());
        }
    }

    @Test
    void readImage_whenInputStreamCausesError_shouldThrowIOException() throws IOException {
        InputStream mockInputStream = mock(InputStream.class);

        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(any(InputStream.class)))
                    .thenThrow(new IOException("Simulated error"));

            // se verifica que la excepción IOException es lanzada
            assertThrows(IOException.class, () -> {
                imageUtil.readImage(mockInputStream);
            });
        }
    }
    //getImageWritter()**

    @Test
    void getImageFormat_whenInputStreamIsValid_shouldReturnString() throws IOException {
        String expectedFormat = "png";

        InputStream mockInputStream = mock(InputStream.class);

        ImageInputStream mockImageInputStream = mock(ImageInputStream.class);

        ImageReader mockImageReader = mock(ImageReader.class);
        when(mockImageReader.getFormatName()).thenReturn(expectedFormat);

        Iterator<ImageReader> mockIterator = Collections.singletonList(mockImageReader).iterator();
        // mocks ImageIO
        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.createImageInputStream(any(InputStream.class)))
                    .thenReturn(mockImageInputStream);
            mockedImageIO.when(() -> ImageIO.getImageReaders(any(ImageInputStream.class)))
                    .thenReturn(mockIterator);

            //prueba del metodo
            String resultFormat = imageUtil.getImageFormat(mockInputStream);
            //se verifica resultados
            assertNotNull(resultFormat, "The format must not be null");
            assertEquals(expectedFormat, resultFormat, "The returned format must be the one expected from the mock.");
            //se verifica llamada a metodos
            mockedImageIO.verify(() -> ImageIO.createImageInputStream(mockInputStream));
            mockedImageIO.verify(() -> ImageIO.getImageReaders(mockImageInputStream));
            verify(mockImageReader).getFormatName();
        }
    }

    @Test
    void getImageFormat_whenNoReadersFound_shouldThrowIOException() {

        InputStream mockInputStream = mock(InputStream.class);
        ImageInputStream mockImageInputStream = mock(ImageInputStream.class);

        // iterador vacio
        Iterator<ImageReader> emptyIterator = Collections.emptyIterator();

        // mocks ImageIO
        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.createImageInputStream(any(InputStream.class)))
                    .thenReturn(mockImageInputStream);

            mockedImageIO.when(() -> ImageIO.getImageReaders(any(ImageInputStream.class)))
                    .thenReturn(emptyIterator); //iterador vacio

            IOException thrown = assertThrows(IOException.class, () -> {
                imageUtil.getImageFormat(mockInputStream);
            }, "Should throw IOException if no readers are found.");

            //se verifica resultados
            assertEquals("Unsupported image format", thrown.getMessage(), "The error message does not match.");

            //se verifica llamada a metodos
            mockedImageIO.verify(() -> ImageIO.createImageInputStream(mockInputStream));
            mockedImageIO.verify(() -> ImageIO.getImageReaders(mockImageInputStream));
        }
    }

    @Test
    void getImageFormat_whenImageInputStreamIsNull_shouldThrowIOException()  {

        InputStream mockInputStream = mock(InputStream.class);

        // mocks de ImageIO
        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            // createImageInputStream devuelve null
            mockedImageIO.when(() -> ImageIO.createImageInputStream(any(InputStream.class)))
                    .thenReturn(null);

            IOException thrown = assertThrows(IOException.class, () -> {
                imageUtil.getImageFormat(mockInputStream);
            }, "IOException should be thrown if createImageInputStream returns null.");

            //se verifica resultados
            assertEquals("Invalid image stream", thrown.getMessage(), "The error message does not match.");

            //se verifica llamada a metodos
            mockedImageIO.verify(() -> ImageIO.createImageInputStream(mockInputStream));
            // getImageReaders() no debe ser llamadoo
            // se verifica que solo se llamó al métodp mockeado
            mockedImageIO.verifyNoMoreInteractions();
        }
    }
    // writeImage()**
    @Test
    void writeImage_ValidInputs_WritesImageSuccessfully() throws IOException, WriteFileException {
        ImageUtil imageUtilSpy = spy(new ImageUtil());

        BufferedImage mockOriginalImage = mock(BufferedImage.class);
        // OutputStream para verificar la salida
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        ImageWriter mockImageWriter = mock(ImageWriter.class);
        ImageWriteParam mockImageWriteParam = mock(ImageWriteParam.class);
        BufferedImage mockProcessedImage = mock(BufferedImage.class);
        ImageOutputStream mockImageOutputStream = mock(ImageOutputStream.class);

        String targetFormat = "png";

        //  getImageWriter() devuelve el mockImageWriter
        doReturn(mockImageWriter).when(imageUtilSpy).getImageWriter(targetFormat);
        //  removeAlphaChannel() devuelve el mockOriginalImage
        doReturn(mockProcessedImage).when(imageUtilSpy).removeAlphaChannel(mockOriginalImage);
        when(mockImageWriter.getDefaultWriteParam()).thenReturn(mockImageWriteParam);
        doNothing().when(mockImageWriter).setOutput(mockImageOutputStream);
        doNothing().when(mockImageWriter).write(isNull(), any(IIOImage.class), eq(mockImageWriteParam));

        try (MockedStatic<ImageIO> mockedStaticImageIO = mockStatic(ImageIO.class)) {
            mockedStaticImageIO.when(() -> ImageIO.createImageOutputStream(mockOutputStream))
                    .thenReturn(mockImageOutputStream);

            imageUtilSpy.writeImage(mockOriginalImage, targetFormat, mockOutputStream);

            // se verifica llamadas a métodos
            verify(imageUtilSpy).getImageWriter(targetFormat);
            verify(imageUtilSpy).removeAlphaChannel(mockOriginalImage);

            // se verifican las interacciones con el mockImageWriter
            verify(mockImageWriter).getDefaultWriteParam();
            verify(mockImageWriter).setOutput(mockImageOutputStream);
            // se verifica que 'write' fue llamado con la imagen procesada y el parámetro
            verify(mockImageWriter).write(isNull(), argThat(iioImage -> iioImage.getRenderedImage() == mockProcessedImage), eq(mockImageWriteParam));

            // se verifica la llamada al método estático
            mockedStaticImageIO.verify(() -> ImageIO.createImageOutputStream(mockOutputStream));

            verifyNoMoreInteractions(mockOriginalImage, mockImageWriter, mockImageWriteParam, mockProcessedImage, mockImageOutputStream);
        }
    }
    @Test
    void writeImage_WhenCreateImageOutputStreamThrowsIOException_ShouldThrowWriteFileException() throws IOException, WriteFileException {
        ImageUtil imageUtilSpy = spy(new ImageUtil());
        BufferedImage mockOriginalImage = mock(BufferedImage.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        String targetFormat = "png";

        doReturn(mock(ImageWriter.class)).when(imageUtilSpy).getImageWriter(targetFormat);
        doReturn(mock(BufferedImage.class)).when(imageUtilSpy).removeAlphaChannel(mockOriginalImage);

        try (MockedStatic<ImageIO> mockedStaticImageIO = mockStatic(ImageIO.class)) {
            mockedStaticImageIO.when(() -> ImageIO.createImageOutputStream(mockOutputStream))
                    .thenThrow(new IOException("Simulated error creating image output stream"));

            // se espera que se lance WriteFileException
            WriteFileException thrown = assertThrows(WriteFileException.class, () -> {
                imageUtilSpy.writeImage(mockOriginalImage, targetFormat, mockOutputStream);
            }, "WriteFileException should be thrown if createImageOutputStream fails.");

            // se verifica el mensaje de la excepción
            assertEquals("Error writing image: Simulated error creating image output stream", thrown.getMessage());
            // se verifica la causa
            assertInstanceOf(IOException.class, thrown.getCause());

            // verificaciones de interacciones
            verify(imageUtilSpy).getImageWriter(targetFormat); // tiene que llamarse
            verify(imageUtilSpy).removeAlphaChannel(mockOriginalImage); // tiene que llamarse
            mockedStaticImageIO.verify(() -> ImageIO.createImageOutputStream(mockOutputStream)); // tiene que llamarse
            //  writer.write() no debe ser llamado
            verifyNoMoreInteractions(mockOriginalImage, mockOutputStream); // se espera que no haya otras interacciones con otros mocks
        }
    }
    @Test
    void writeImage_WhenWriterThrowsIOException_ShouldThrowWriteFileException() throws IOException, WriteFileException {
        ImageUtil imageUtilSpy = spy(new ImageUtil());
        BufferedImage mockOriginalImage = mock(BufferedImage.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        ImageWriter mockImageWriter = mock(ImageWriter.class);
        ImageWriteParam mockImageWriteParam = mock(ImageWriteParam.class);
        BufferedImage mockProcessedImage = mock(BufferedImage.class);
        ImageOutputStream mockImageOutputStream = mock(ImageOutputStream.class);

        String targetFormat = "jpeg";

        // Comportamiento del spy
        doReturn(mockImageWriter).when(imageUtilSpy).getImageWriter(targetFormat);
        doReturn(mockProcessedImage).when(imageUtilSpy).removeAlphaChannel(mockOriginalImage);

        // Comportamiento del mockImageWriter
        when(mockImageWriter.getDefaultWriteParam()).thenReturn(mockImageWriteParam);
        doNothing().when(mockImageWriter).setOutput(mockImageOutputStream);
        // writer.write() debe lanzar IOException
        doThrow(new IOException("Simulated image write error")).when(mockImageWriter).write(isNull(), any(IIOImage.class), eq(mockImageWriteParam));

        // método estático ImageIO.createImageOutputStream()
        try (MockedStatic<ImageIO> mockedStaticImageIO = mockStatic(ImageIO.class)) {
            mockedStaticImageIO.when(() -> ImageIO.createImageOutputStream(mockOutputStream))
                    .thenReturn(mockImageOutputStream);

            WriteFileException thrown = assertThrows(WriteFileException.class, () -> {
                imageUtilSpy.writeImage(mockOriginalImage, targetFormat, mockOutputStream);
            }, "Should throw WriteFileException if ImageWriter.write() fails.");

            assertEquals("Error writing image: Simulated image write error", thrown.getMessage());
            assertTrue(thrown.getCause() instanceof IOException);

            // Verificaciones de interacciones
            verify(imageUtilSpy).getImageWriter(targetFormat);
            verify(imageUtilSpy).removeAlphaChannel(mockOriginalImage);
            verify(mockImageWriter).getDefaultWriteParam();
            verify(mockImageWriter).setOutput(mockImageOutputStream);
            verify(mockImageWriter).write(isNull(), argThat(iioImage -> iioImage.getRenderedImage() == mockProcessedImage), eq(mockImageWriteParam));
            mockedStaticImageIO.verify(() -> ImageIO.createImageOutputStream(mockOutputStream));

            verifyNoMoreInteractions(mockOriginalImage, mockOutputStream, mockImageWriter, mockImageWriteParam, mockProcessedImage, mockImageOutputStream);
        }
    }
    @Test
    void writeImage_WhenGetImageWriterThrowsException_ShouldThrowWriteFileException() throws IOException, WriteFileException {
        ImageUtil imageUtilSpy = spy(new ImageUtil());
        BufferedImage mockOriginalImage = mock(BufferedImage.class);
        OutputStream mockOutputStream = mock(OutputStream.class);
        String targetFormat = "unsupported"; // Un formato que no tendrá un writer

        // se espera que getImageWriter() lance una WriteFileException
        doThrow(new WriteFileException("No ImageWriter found for format: " + targetFormat))
                .when(imageUtilSpy).getImageWriter(targetFormat);

        // se mockea ImageIO estáticamente
        try (MockedStatic<ImageIO> mockedStaticImageIO = mockStatic(ImageIO.class, CALLS_REAL_METHODS)) { // CALLS_REAL_METHODS es opcional aquí

            WriteFileException thrown = assertThrows(WriteFileException.class, () -> {
                imageUtilSpy.writeImage(mockOriginalImage, targetFormat, mockOutputStream);
            }, "WriteFileException should be thrown if getImageWriter fails.");

            assertEquals("No ImageWriter found for format: " + targetFormat, thrown.getMessage());

            // solo se debe haber intentado llamar a getImageWriter()
            verify(imageUtilSpy).getImageWriter(targetFormat);
            // Se verifica que otros meotodos no sean llamados
            verify(imageUtilSpy, never()).removeAlphaChannel(any());
            verifyNoInteractions(mockOutputStream); // No se debería haber interactuado con el OutputStream
            // No se debe haber llamado a ningún método de ImageIO estático
            mockedStaticImageIO.verifyNoMoreInteractions();
        }
    }
}
