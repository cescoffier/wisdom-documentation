package site.upload;

public class VersionExtractor {


    public static String versionFromReferenceDocumentation(String filename) {
        return filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf("."));
    }

    public static String versionFromMojoDocumentation(String filename) {
        return filename.substring("wisdom-maven-plugin-".length(), filename.lastIndexOf("-"));
    }

    public static String versionFromJavaDoc(String filename) {
        return filename.substring("wisdom-framework-".length(), filename.lastIndexOf("-"));
    }
}
