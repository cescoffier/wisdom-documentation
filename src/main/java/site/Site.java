package site;

import org.apache.felix.ipojo.annotations.Requires;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.cache.Cached;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

/**
 * The main controller serving most of the resources.
 * <p/>
 * As the content is written using Asciidoc, the access to HTML resources is intercepted to be loading within the
 * site template. The same method is used for the Mojo documentation.
 */
@Controller
public class Site extends DefaultController {

    @Requires
    ApplicationConfiguration configuration;

    @View("home")
    private Template home;

    /**
     * Template used to render content generated from Asciidoc.
     */
    @View("content/asciidoc")
    private Template asciidoc;

    /**
     * Template used to render content generated from Maven Site.
     */
    @View("content/mojo")
    private Template mojo;

    @Cached(key = "home", duration = 3600)
    @Route(method = HttpMethod.GET, uri = "/")
    public Result home() {
        // The main documentation linked is bound to the version set in the configuration file.
        return ok(render(home, "wisdom.version", configuration.get("wisdom.version")));
    }

    @Route(method = HttpMethod.GET, uri = "/learn/{path+}")
    public Result page(@Parameter("path") String path) {
        if (path == null || path.isEmpty()) {
            return ok(render(asciidoc, "page", "/assets/site/learn.html"));
        } else {
            return ok(render(asciidoc, "page", "/assets/site/" + path));
        }
    }

    @Route(method = HttpMethod.GET, uri = "/reference/{path+}")
    public Result doc(@Parameter("path") String path) {
        if (context().request().accepts(MimeTypes.HTML)) {
            // Wraps the html pages in the layout.
            return ok(render(asciidoc, "page", "/documentation/reference/" + path));
        } else {
            return redirect("/documentation/reference/" + path);
        }
    }

    @Route(method = HttpMethod.GET, uri = "/wisdom-maven-plugin/{path+}")
    public Result mojo(@Parameter("path") String path) {
        if (context().request().accepts(MimeTypes.HTML)) {
            // Wraps the html pages in the layout.
            return ok(render(mojo, "page", "/documentation/wisdom-maven-plugin/" + path));
        } else {
            return redirect("/documentation/wisdom-maven-plugin/" + path);
        }
    }

    @Cached(key = "learn", duration = 3600)
    @Route(method = HttpMethod.GET, uri = "/learn")
    public Result learn() {
        return ok(render(asciidoc, "page", "/assets/site/learn.html"));
    }


}
