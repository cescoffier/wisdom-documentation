function getItems(list, current, factory) {
    var acc = $("<span>");
    $(list).each(function(idx, item) {
        if (acc.children().size() == 0) {
            acc.append(factory(item, current));
        } else {
            acc = acc.append(" - ").append(factory(item, current));
        }
    });
    return acc;
}

function buildReferenceLink(item, current) {
    var link = $("<a>").attr("href", "/reference/" + item + "/index.html").html(item);
    if (item.match(/-SNAPSHOT/)) {
        link.addClass("text-muted");
    }

    if (item === current) {
        $(link).attr("style", "font-weight:bold");
    }

    return link;
}

function buildMojoLink(item, current) {
    var link = $("<a>").attr("href", "/wisdom-maven-plugin/" + item + "/plugin-info.html").html(item);
    if (item.match(/-SNAPSHOT/)) {
        link.addClass("text-muted");
    }

    if (item === current) {
        $(link).attr("style", "font-weight:bold");
    }

    return link;
}

function buildJavadocLink(item, current) {
    var link = $("<a>").attr("href", "/apidocs/" + item + "/index.html").html(item);
    if (item.match(/-SNAPSHOT/)) {
        link.addClass("text-muted");
    }

    if (item === current) {
        $(link).attr("style", "font-weight:bold");
    }

    return link;
}



function processVersions(versions) {
    // Retrieve the current version.
    var current = versions["current"];
    var list = $("<ul>");

    var ref = $("<li/>").append("Reference Documentation&nbsp;-&nbsp;")
        .append(getItems(versions["reference"], current, buildReferenceLink));

    var maven = $("<li/>").append("Wisdom Maven Plugin Documentation&nbsp;-&nbsp;")
        .append(getItems(versions["mojo"], current, buildMojoLink));

    var javadoc = $("<li/>").append("JavaDoc&nbsp;-&nbsp;")
        .append(getItems(versions["javadoc"], current, buildJavadocLink));

    list.append(ref);
    list.append(maven);
    list.append(javadoc);

    $("#version-list").empty().append(list);
}
$(document).ready(function() {
    $.getJSON("/upload/versions.json", processVersions);
});