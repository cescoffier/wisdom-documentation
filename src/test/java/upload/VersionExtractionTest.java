package upload;

import org.junit.Test;
import site.upload.VersionExtractor;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionExtractionTest {


    @Test
    public void testExtractionOfTheVersionForReference() {
        String filename = "documentation-0.5-SNAPSHOT.jar";
        assertThat(VersionExtractor.versionFromReferenceDocumentation(filename)).isEqualTo("0.5-SNAPSHOT");

        filename = "documentation-0.4.1.jar";
        assertThat(VersionExtractor.versionFromReferenceDocumentation(filename)).isEqualTo("0.4.1");

        filename = "documentation-0.4.jar";
        assertThat(VersionExtractor.versionFromReferenceDocumentation(filename)).isEqualTo("0.4");
    }

    @Test
    public void testExtractionOfTheVersionForMojoDoc() {
        String filename = "wisdom-maven-plugin-0.5.1-site.jar";
        assertThat(VersionExtractor.versionFromMojoDocumentation(filename)).isEqualTo("0.5.1");

        filename = "wisdom-maven-plugin-0.5-site.jar";
        assertThat(VersionExtractor.versionFromMojoDocumentation(filename)).isEqualTo("0.5");

        filename = "wisdom-maven-plugin-0.6-SNAPSHOT-site.jar";
        assertThat(VersionExtractor.versionFromMojoDocumentation(filename)).isEqualTo("0.6-SNAPSHOT");
    }

    @Test
    public void testExtractionOfTheVersionForJavaDoc() {
        String filename = "wisdom-framework-0.5.1-javadoc.jar";
        assertThat(VersionExtractor.versionFromJavaDoc(filename)).isEqualTo("0.5.1");

        filename = "wisdom-framework-0.5-javadoc.jar";
        assertThat(VersionExtractor.versionFromJavaDoc(filename)).isEqualTo("0.5");

        filename = "wisdom-framework-0.6-SNAPSHOT-javadoc.jar";
        assertThat(VersionExtractor.versionFromJavaDoc(filename)).isEqualTo("0.6-SNAPSHOT");
    }

}
