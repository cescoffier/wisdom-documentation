package site.release;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.annotations.scheduler.Every;
import org.wisdom.api.configuration.ApplicationConfiguration;
import org.wisdom.api.content.Json;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A controller serving the Wisdom Release Notes.
 */
@Controller
public class ReleaseNoteController extends DefaultController {

    @View("release/release")
    Template template;

    @Requires
    ApplicationConfiguration configuration;

    @Requires
    Json json;

    private List<Release> releases;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNoteController.class);

    @Validate
    @Every("6h")
    public void populateCache() throws Exception {
        retrieveReleases();
    }

    @Route(method = HttpMethod.GET, uri = "/releases")
    public Result index() {
        return ok(render(template, "releases", releases));
    }

    public void retrieveReleases() throws Exception {
        LOGGER.info("Retrieving the release notes for " + configuration.get("repo.owner") + "/" + configuration.get
                ("repo.name"));
        List<Release> list = new ArrayList<Release>();
        String milestones = "https://api.github.com/repos/" + configuration.get("repo.owner") + "/" + configuration.get
                ("repo.name") + "/milestones?state=closed";
        final JsonNode body = json.parse(Request.Get(milestones).execute().returnContent().asString());
        if (body instanceof ObjectNode) {
            LOGGER.error("Cannot retrieve the milestones from GitHub - Probably an API Rate Limit. The response is: " +
                    "{}", body);
            return;
        }
        ArrayNode array = (ArrayNode) body;
        for (JsonNode node : array) {
            LOGGER.info("Retrieving the issue list for {}", node.get("title").asText());
            final String updated_at = node.get("updated_at").asText();
            String d = updated_at.substring(0, updated_at.indexOf("T"));

            Release release = new Release(node.get("title").asText(), d);
            int milestoneNumber = node.get("number").asInt();
            String url = "https://api.github.com/repos/" + configuration.get("repo.owner") + "/" + configuration.get
                    ("repo.name") + "/issues?milestone=" + milestoneNumber + "&state=closed&per_page=300";
            final JsonNode resp = json.parse(Request.Get(url).execute().returnContent().asString());
            if (resp instanceof ObjectNode) {
                LOGGER.error("Cannot retrieve the issues from GitHub - Probably an API Rate Limit. The response is: " +
                        "{}", body);
                return;
            }
            ArrayNode issues = (ArrayNode) resp;
            for (JsonNode issue : issues) {
                release.issues.add(new Issue(issue.get("title").asText(), issue.get("number").asInt(),
                        issue.get("html_url").asText()));
            }
            LOGGER.info("{} issues have been added to release {}", release.issues.size(), release.name);
            list.add(release);
        }

        Collections.sort(list, new Comparator<Release>() {
            @Override
            public int compare(Release o1, Release o2) {
                return o2.date.compareTo(o1.date);
            }
        });

        synchronized (this) {
            this.releases = list;
        }
        LOGGER.info("Releases updated");
    }

}
