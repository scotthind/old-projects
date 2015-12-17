<?php //Authors: Joseph Smith and Christopher Bowen

// This function generates the filtered text that the user is searching for.
function generate_query($query_text, $search_text, $filter) 
{
    // Remove spaces before and after the search term.
    $search_text = trim($search_text);	
    // Test for empty strings
    if($search_text != "" && $search_text != NULL)
    {
        // Search by department, email, phone, or name
	switch($filter)
        {
            // Search by department
            case 'department':
                $query_text .= " Department.deptName LIKE '%".$search_text."%'";
		break;
            // Search by email
            case 'email':
		$query_text .= " email LIKE '%".$search_text."%'";
		break;
            // Search by phone number or extension
            case 'phone':
		$query_text .= " phone LIKE '%".$search_text."%'";
		break;
            // Search by first or last name
            case 'name':
		default:
                    $query_text .= " firstName LIKE '%".$search_text."%'";
                    $query_text .= " OR lastName LIKE '%".$search_text."%'";
                    break;
	}
    }
    return $query_text;
}

// Set the active SQLite database
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
if(!$db) 
{
    die ($sqliteerror);
}

// Grab the variables that were posted through AJAX.
$search_text = $_REQUEST['search_text'];
$filter = $_REQUEST['filter'];

// Initial SELECT query string without any filtering. This will get concatinated to
// a string with filtering if the user wants to filter their search.
$query_text = "SELECT cubeID, cubeNumber, cubicle.personID, loclat, loclong, email, phone, Department.deptName, firstName, lastName, iconPath FROM cubicle natural join Personnel natural join Department natural join Icons WHERE";

// Split the text in the search boxes by ","'s. This is if they want to search for
// multiple items.
$exploded_string = explode(",", $search_text);

// If they there is a "," in the search term, they want to search for multiple items.
if(count($exploded_string) > 1)
{	
    for($i = 0; $i < count($exploded_string); $i++)
    {
        $query_text = generate_query($query_text, $exploded_string[$i], $filter);
        if($i < count($exploded_string) - 1)
        {
            $query_text .= " OR";
        }
    }
}
else
{
    // Otherwise, just search for 1 item.
    $query_text = generate_query($query_text, $search_text, $filter);
}

// Select results that match our search fields from the database.
$results_query = sqlite_query($db, $query_text);

// Display the search results by echoing back HTML to the AJAX/Javascript
// which will display the results to the user in plain text.
if(sqlite_num_rows($results_query) > 0)
{
    while($results_row = @sqlite_fetch_array($results_query, SQLITE_ASSOC))
    {
        echo "<p id=\"search_result\"><strong>";
	echo $results_row['firstName'];
	echo " ";
        echo $results_row['lastName'];
	echo "</strong><br />";
        echo "Dept: ".$results_row['Department.deptName'];
	echo "<br />";
        echo "Office: ".$results_row['cubeNumber'];
	echo "<br />";
	echo "Email: ".$results_row['email'];
	echo "<br />";
	echo "Phone: ".$results_row['phone'];
	echo "<input type=\"hidden\" name=\"personID\" value=\"".$results_row['cubicle.personID']."\">";
	echo "</p>";
	echo "<hr id=\"results_divider\" />";
    }
}
else
{
    // There were no results matching the specified keywords.
    echo "Sorry. No results were found.";
}
?>