package site.release;

import org.osgi.framework.Version;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release implements Comparable<Release> {

    public final String name;

    public final String date;

    public final List<Issue> issues = new ArrayList<Issue>();


    public Release(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public Version version() {
        return new Version(name);
    }

    @Override
    public int compareTo(Release other) {
        return other.version().compareTo(version());
    }
}
