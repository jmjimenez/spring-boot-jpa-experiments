package es.jmjg.experiments.suite;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

@Suite
@SelectPackages("es.jmjg.experiments")
@IncludeClassNamePatterns(".*Test$")
@ExcludeClassNamePatterns(".*IntegrationTest$")
@ActiveProfiles("test")
@Tag("unit")
public class UnitTestSuite {
}
