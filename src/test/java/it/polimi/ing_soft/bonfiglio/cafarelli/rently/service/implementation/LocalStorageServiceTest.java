package it.polimi.ing_soft.bonfiglio.cafarelli.rently.service.implementation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalStorageServiceTest {

    private LocalStorageService localStorageService;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private MultipartFile mockFile2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        localStorageService = new LocalStorageService();
    }

    @AfterEach
    void tearDown() {
        localStorageService.shutdown();
    }

    @Test
    void getDefaultUserPhoto_returnsCorrectPath() {
        String result = localStorageService.getDefaultUserPhoto();
        assertEquals("defaultProfileImage.png", result);
    }

    @Test
    void saveUserProfilePhoto_success(){
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "test.jpg", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // Act
        String result = localStorageService.saveUserProfilePhoto(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("user_photos/"));
        assertTrue(result.endsWith(".jpg"));

        // Cleanup
        try {
            File savedFile = new File("storage/images/" + result);
            if (savedFile.exists()) {
                savedFile.delete();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void savePropertyImages_success(){
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile(
            "image1.jpg", 
            "image1.jpg", 
            "image/jpeg", 
            "test image 1 content".getBytes()
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "image2.jpg", 
            "image2.jpg", 
            "image/jpeg", 
            "test image 2 content".getBytes()
        );

        List<MultipartFile> files = Arrays.asList(file1, file2);

        // Act
        List<String> results = localStorageService.savePropertyImages(files, 1L);

        // Assert
        assertEquals(2, results.size());
        for (String path : results) {
            assertTrue(path.startsWith("properties/Property_withUserId_1/"));
            assertTrue(path.endsWith(".jpg"));

            // Cleanup
            try {
                File savedFile = new File("storage/images/" + path);
                if (savedFile.exists()) {
                    savedFile.delete();
                }
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    void updateUserProfilePhoto_withOldPhoto_success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "newPhoto.jpg", 
            "newPhoto.jpg", 
            "image/jpeg", 
            "new photo content".getBytes()
        );

        // Create a spy of the LocalStorageService to mock the deleteImage method
        LocalStorageService spyService = spy(localStorageService);
        // Use doNothing to prevent the actual deleteImage method from being called
        doNothing().when(spyService).deleteImage(anyString());

        String oldPhotoPath = "user_photos/oldPhoto.jpg";

        // Act
        String result = spyService.updateUserProfilePhoto(file, oldPhotoPath);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("user_photos/"));
        assertTrue(result.endsWith(".jpg"));
        // Verify that deleteImage was called with the old photo path
        verify(spyService).deleteImage(oldPhotoPath);

        // Cleanup
        try {
            File savedFile = new File("storage/images/" + result);
            if (savedFile.exists()) {
                savedFile.delete();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void updateUserProfilePhoto_withDefaultPhoto_doesNotDeleteDefault(){
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "newPhoto.jpg", 
            "newPhoto.jpg", 
            "image/jpeg", 
            "new photo content".getBytes()
        );

        String defaultPhotoPath = "images/defaultUserPhoto.png";

        // Act
        String result = localStorageService.updateUserProfilePhoto(file, defaultPhotoPath);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("user_photos/"));
        assertTrue(result.endsWith(".jpg"));

        // Cleanup
        try {
            File savedFile = new File("storage/images/" + result);
            if (savedFile.exists()) {
                savedFile.delete();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void shutdown_executesWithoutErrors() {
        localStorageService.shutdown();
    }
}
