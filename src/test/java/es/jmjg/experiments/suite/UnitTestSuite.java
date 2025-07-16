package es.jmjg.experiments.suite;

import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("es.jmjg.experiments")
@IncludeClassNamePatterns(".*Test$")
@ExcludeClassNamePatterns(".*IntegrationTest$")
@Tag("unit")
public class UnitTestSuite {
}