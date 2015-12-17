/**
 * Authors: Joseph Smith and Christopher Bowen
 */

// Search Box Functionality
$(document).ready(function () {
    // Filter search by narrowing down to a specific category
    // Default: "none"
    var filter = "name";

    // When something is changed in Search Input Box, perform an AJAX search
    $("#search").bind('keyup keydown update', function () {
        var search_text = jQuery.trim($("#search").val());

        if (search_text != "" && search_text != null) {
            $.post("search.php", { search_text: escape(search_text), filter: filter }, function (data) {
                $("#search_results").html(data);
            });
        } else {
            $("#search_results").html("Sorry. No results were found.");
        }
    });

    $("#search_result").live('click', function () {
        var employeeID = $(this).children("input[id=employeeID]").val();

        showSearchSelected(employeeID);
    });

    // Change an Advanced Search setting
    $("#adv_options").change(function () {
        filter = $("#search_filter").val();
    });

    $("#search").tipsy({ gravity: 's' });
    $("#search_arrow").tipsy({ gravity: 'ne' });
    $("#login_text").tipsy({ gravity: 'n' });
    $("#gohome").tipsy({ gravity: 'n' });
    $("#level1").tipsy({ gravity: 's' });
    $("#level2").tipsy({ gravity: 's' });
    $("#settings").tipsy({ gravity: 'ne' });
});

/**
 * This function slides the search options bar up and down on selection.
 */
function advSearch() {
	$("#adv_options").slideToggle();
}
