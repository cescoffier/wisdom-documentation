package site;

import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Parameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

/**
 * Created by clement on 16/03/2014.
 */
@Controller
public class Site extends DefaultController {

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

    @Route(method = HttpMethod.GET, uri = "/")
    public Result home() {
        return ok(render(home));
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

    @Route(method = HttpMethod.GET, uri = "/learn")
    public Result learn() {
        return ok(render(asciidoc, "page", "/assets/site/learn.html"));
    }


}
