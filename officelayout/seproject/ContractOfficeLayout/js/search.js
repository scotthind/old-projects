// Search Box Functionality
$(document).ready(function () {
    // Filter search by narrowing down to a specific category
    // Default: "none"
    var filter = "name";

    // When something is changed in Search Input Box, perform an AJAX search
    $("#search").bind('keyup keydown update', function () {
        var search_text = jQuery.trim($("#search").val());

        if (search_text != "" && search_text != null) {
            $.post("search.php", { search_text: search_text, filter: filter }, function (data) {
                $("#search_results").html(data);
            });
        } else {
            $("#search_results").html("Sorry. No results were found.");
        }
    });

    $("#search_result").live('click', function () {
        var personID = $(this).children("input[name=personID]").val();

        showSearchSelected(personID);
    });

    // Change an Advanced Search setting
    $("#adv_options").change(function () {
        filter = $("#search_filter").val();
    });

    $("#search").tipsy({ gravity: 's' });
    $("#search_arrow").tipsy({ gravity: 'ne' });
    $("#login_text").tipsy({ gravity: 'n' });
    $("#back_home").tipsy({ gravity: 'n' });
    $("#level1").tipsy({ gravity: 's' });
    $("#level2").tipsy({ gravity: 's' });
    $("#settings").tipsy({ gravity: 'ne' });
});

function advSearch() {
	$("#adv_options").slideToggle();
}