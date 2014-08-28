$(document).ready(function () {
    //load news list

    load();

});





/* list all available extension in a table */
function writeExtensionData(data) {
    alert("my friend the alert box!");
    $("#news-section").empty();
    $("<h1></h1>").addClass("highlight-color").html("News").appendTo("#news-section");
    $.each(data, function (index,value) {
        console.log(index,value);

        //create a new row using the extensions key as a collasable link
        var article = $("<div></div>").addClass("article col-xs-12").appendTo("#news-section");
        var title = $("<div></div>").addClass("title col-xs-12 col-sm-6").appendTo(article);
        var date = $("<div></div>").addClass("date col-xs-12 col-sm-6").html(value.dateCreated).appendTo(article);
        title.append($("<a></a>")
            .attr("href", "#collapse" + value.title)
            .attr("data-toggle", "collapse")
            .html(value.title));

        //create a list of the information for each key
        var content = $("<div></div>").toggleClass("properties").addClass("articlecontent col-xs-12").html(value.content).appendTo(article);

       // title.append(content);
       // article.append(title).append(date).appendTo("news-section");

        });

}

/*load the list of extensions as json */
function load() {
    console.log(window.location.host);
    //$.get("http://" + window.location.host + "/registry/list").success(writeExtensionData)
    $.get("http://localhost:9000/assets/newsstream.json").success(writeExtensionData);
}

/**
 * @return {boolean}
 */
function HasSubstring(string, substring) {
    return string.indexOf(substring) > -1;

}
