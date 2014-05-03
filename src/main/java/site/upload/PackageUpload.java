package site.upload;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Attribute;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.http.FileItem;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;
import org.wisdom.monitor.extensions.security.MonitorAuthenticator;
import org.wisdom.monitor.service.MonitorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Manages the upload of the documentation packages (mojo site, reference documentation, javadoc)
 * It parses the version from the file name so it must be a valid Maven artifact using the convention
 * artifactId-version.
 */
@Controller
@Authenticated(MonitorAuthenticator.class)
public class PackageUpload extends DefaultController implements MonitorExtension {

    public final static String DOCUMENTATION_ARTIFACT_ID = "documentation";

    public final static String MOJO_ARTIFACT_ID = "wisdom-maven-plugin";
    public final static String MOJO_CLASSIFIER = "site";

    public final static String JAVADOC_ARTIFACT_ID = "wisdom-framework";
    public final static String JAVADOC_CLASSIFIER = "javadoc";

    public final static Logger LOGGER = LoggerFactory.getLogger(PackageUpload.class);

    @Requires
    ApplicationConfiguration configuration;

    @View("upload/upload")
    Template template;

    File root;
    private File referenceRoot;
    private File javadocRoot;
    private File mojoRoot;

    private File storage;

    @Validate
    public void validate() {
        root = new File(configuration.getBaseDir(), "documentation");
        referenceRoot = new File(root, "reference");
        javadocRoot = new File(root, "apidocs");
        mojoRoot = new File(root, "wisdom-maven-plugin");
        storage = new File(root, "storage");

        LOGGER.debug("Creating {} : {}", referenceRoot.getAbsolutePath(), referenceRoot.mkdirs());
        LOGGER.debug("Creating {} : {}", javadocRoot.getAbsolutePath(), javadocRoot.mkdirs());
        LOGGER.debug("Creating {} : {}", mojoRoot.getAbsolutePath(), mojoRoot.mkdirs());
        LOGGER.debug("Creating {} : {}", storage.getAbsolutePath(), storage.mkdirs());
    }

    @Route(method = HttpMethod.GET, uri = "/upload")
    public Result upload() {
        Collection<File> archives = FileUtils.listFiles(storage, null, false);
        return ok(render(template, "files", archives));
    }

    @Route(method = HttpMethod.POST, uri = "/upload/reference")
    public Result uploadReferenceDocumentation(@Attribute("reference") final FileItem upload) throws IOException {
        String fileName = upload.name();
        LOGGER.info("Uploading reference documentation : " + fileName);
        if (!fileName.startsWith(DOCUMENTATION_ARTIFACT_ID)) {
            return badRequest("The upload file does not look like the reference documentation");
        }

        final String version = fileName.substring(DOCUMENTATION_ARTIFACT_ID.length() + 1, fileName.length() - ".jar".length());
        LOGGER.info("Extracting version from " + fileName + " : " + version);

        if (Strings.isNullOrEmpty(version)) {
            return badRequest("The upload file does not look like the reference documentation - malformed version");
        }

        // Start asynchronous from here
        return async(
                new Callable<Result>() {
                    @Override
                    public Result call() throws Exception {
                        File docRoot = new File(referenceRoot, version);
                        if (docRoot.isDirectory()) {
                            LOGGER.info("The directory {} already exists - cleanup", docRoot.getAbsolutePath());
                            FileUtils.deleteQuietly(docRoot);
                        }
                        LOGGER.debug("Creating {} : {}", docRoot.getAbsolutePath(), docRoot.mkdirs());
                        // Unpacking /assets/ to docRoot
                        unpack(upload.stream(), "assets/", docRoot);
                        store(upload);
                        return redirect("/upload");
                    }
                }
        );
    }

    @Route(method = HttpMethod.POST, uri = "/upload/mojo")
    public Result uploadMojoDocumentation(@Attribute("mojo") final FileItem upload) throws IOException {
        String fileName = upload.name();
        LOGGER.info("Uploading mojo documentation : " + fileName);
        if (!fileName.startsWith(MOJO_ARTIFACT_ID)) {
            return badRequest("The upload file does not look like the mojo documentation");
        }

        final String version = fileName.substring(MOJO_ARTIFACT_ID.length() + 1,
                fileName.length() - ("-" + MOJO_CLASSIFIER + ".jar").length());
        LOGGER.info("Extracting version from " + fileName + " : " + version);

        if (Strings.isNullOrEmpty(version)) {
            return badRequest("The upload file does not look like the mojo documentation - malformed version");
        }

        // Start asynchronous from here
        return async(
                new Callable<Result>() {
                    @Override
                    public Result call() throws Exception {
                        File docRoot = new File(mojoRoot, version);
                        if (docRoot.isDirectory()) {
                            LOGGER.info("The directory {} already exists - cleanup", docRoot.getAbsolutePath());
                            FileUtils.deleteQuietly(docRoot);
                        }
                        LOGGER.debug("Creating {} : {}", docRoot.getAbsolutePath(), docRoot.mkdirs());
                        // No prefix.
                        unpack(upload.stream(), "", docRoot);
                        store(upload);
                        return redirect("/upload");
                    }
                }
        );
    }

    @Route(method = HttpMethod.POST, uri = "/upload/javadoc")
    public Result uploadJavadoc(@Attribute("apidocs") final FileItem upload) throws IOException {
        String fileName = upload.name();
        LOGGER.info("Uploading JavaDoc : " + fileName);
        if (!fileName.startsWith(JAVADOC_ARTIFACT_ID)) {
            return badRequest("The upload file does not look like the JavaDoc package");
        }

        final String version = fileName.substring(JAVADOC_ARTIFACT_ID.length() + 1,
                fileName.length() - ("-" + JAVADOC_CLASSIFIER + ".jar").length());
        LOGGER.info("Extracting version from " + fileName + " : " + version);

        if (Strings.isNullOrEmpty(version)) {
            return badRequest("The upload file does not look like the JavaDoc package - malformed version");
        }

        // Start asynchronous from here
        return async(
                new Callable<Result>() {
                    @Override
                    public Result call() throws Exception {
                        File docRoot = new File(javadocRoot, version);
                        if (docRoot.isDirectory()) {
                            LOGGER.info("The directory {} already exists - cleanup", docRoot.getAbsolutePath());
                            FileUtils.deleteQuietly(docRoot);
                        }
                        LOGGER.debug("Creating {} : {}", docRoot.getAbsolutePath(), docRoot.mkdirs());
                        // No prefix.
                        unpack(upload.stream(), "", docRoot);
                        store(upload);
                        return redirect("/upload");
                    }
                }
        );
    }

    private void unpack(InputStream stream, String prefix, File destination) throws IOException {
        try {
            ZipInputStream zis = new ZipInputStream(stream);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String entryName = ze.getName();
                if ((prefix == null || entryName.startsWith(prefix)) && !entryName.endsWith("/")) {
                    String stripped;
                    if (prefix != null) {
                        stripped = entryName.substring(prefix.length());
                    } else {
                        stripped = entryName;
                    }

                    LOGGER.info("Extracting " + entryName + " -> " + destination + File.separator + stripped + "...");
                    File f = new File(destination + File.separator + stripped);
                    f.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(f);
                    int len;
                    byte buffer[] = new byte[1024];
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void store(FileItem upload) throws IOException {
        LOGGER.info("Storing " + upload.name());
        File stored = new File(storage, upload.name());
        FileUtils.writeByteArrayToFile(stored, upload.bytes());
    }

    /**
     * @return the label displayed in the menu.
     */
    @Override
    public String label() {
        return "Upload";
    }

    /**
     * @return the url of the extension page.
     */
    @Override
    public String url() {
        return "/upload";
    }

    /**
     * @return the category of the extension such as "root", "wisdom" or "OSGi".
     */
    @Override
    public String category() {
        return "Documentation";
    }
}
