package site;

import org.apache.felix.ipojo.configuration.Configuration;
import org.apache.felix.ipojo.configuration.Instance;

/**
 * Declares an instance of the asset controller to server $basedir/documentation.
 * The goal is to have the external documentation (reference, mojo and javadoc) structured as follows:
 *
 * {@literal documentation/reference/0.4}
 * {@literal documentation/reference/0.5-SNAPSHOT}
 * {@literal documentation/wisdom-maven-plugin/0.4}
 * {@literal documentation/wisdom-maven-plugin/0.5-SNAPSHOT}
 * {@literal documentation/apidocs/0.4}
 * {@literal documentation/apidocs/0.5-SNAPSHOT}
 * ...
 */
@Configuration
public class DocConfiguration {

    /**
     * Declares the instance of resource controller serving resources from 'basedir/documentation'.
     */
    Instance instance = Instance.instance().of("org.wisdom.resources.ResourceController")
            .named("Documentation-Resources")
            .with("path").setto("documentation");
}
