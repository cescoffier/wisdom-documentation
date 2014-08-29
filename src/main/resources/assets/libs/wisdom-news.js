$(document).ready(function () {
    //load news list
    load();
});

/* list all available news */
function writeNewsData(data) {
    $("#news-section").empty();
    $("<h1></h1>").addClass("highlight-color").html("News").appendTo("#news-section");
    $.each(data, function (index,value) {
        var newsId = value.id;
        //replace orientdb id formatting for html tag compatibility
        if (HasSubstring(newsId, "#")) {
            newsId = newsId.replace("#", "-");
        }
        if (HasSubstring(newsId, ":")) {
            newsId = newsId.replace(":", "-");
        }
        //create a new row using the news key as a collapsible link
        var article = $("<div></div>").addClass("article col-xs-12").appendTo("#news-section");
        var title = $("<div></div>").addClass("title col-xs-6").appendTo(article);
        var date = $("<div></div>").addClass("date col-xs-6").html(value.dateCreated).appendTo(article);
        var icon = $("<i></i>").addClass("fa fa-leaf fa-fw leaf");
        title.append($("<a>"+icon+"</a>")
            .attr("href", "#collapse" + newsId)
            .attr("data-toggle", "collapse")
            .html("&nbsp;"+value.title));
        icon.prependTo(title);
        //create a collapsible list of the information for each key
        var content = $("<div></div>").attr("id","collapse"+newsId).addClass("articlecontent col-xs-12 collapse").html(value.content).appendTo(article);

        });

}

/*load the list of news as json */
function load() {
    $.get("http://localhost:9000/assets/newsstream.json").success(writeNewsData);
}

/**
 * @return {boolean}
 */
function HasSubstring(string, substring) {
    return string.indexOf(substring) > -1;

}
