package site.release;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release {

    public final String name;

    public final String date;

    public final List<Issue> issues = new ArrayList<Issue>();


    public Release(String name, String date) {
        this.name = name;
        this.date = date;
    }
}
