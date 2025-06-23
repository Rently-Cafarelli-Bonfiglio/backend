package it.polimi.ing_soft.bonfiglio.cafarelli.rently;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Main test suite for the Rently application.
 * <p>
 * This class serves as a test suite that includes all tests in the application package.
 * It uses JUnit 5's suite capabilities to run all tests within the specified package.
 * The SpringBootTest annotation ensures that the Spring context is loaded for integration tests.
 * </p>
 */
@Suite
@SuiteDisplayName("Rently Application Tests Suite")
@SelectPackages("it.polimi.ing_soft.bonfiglio.cafarelli.rently")
@SpringBootTest
class RentlyApplicationTests {

}
