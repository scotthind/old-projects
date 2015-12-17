<?php //Authors: Joseph Smith and Christopher Bowen

/**
 * This function generates the filtered text that the user is searching for.
 * @param $query_text Search query being constructed
 * @param $search_text String the user inputs to execute a search
 * @param $filter Criterion we are searching by
 * @return The augmented query text
 */
function generate_query($query_text, $search_text, $filter) 
{
    // Remove spaces before and after the search term.
    $search_text = trim($search_text);
    // Test for empty strings
    if($search_text != "" && $search_text != NULL)
    {
        // Search by department, e-mail, phone, or name
		switch($filter)
        {
            // Search by department
            case 'department':
                $query_text .= " DeptName LIKE '%".$search_text."%'";
				break;
           	// Search by e-mail
            case 'email':
				$query_text .= " Email LIKE '%".$search_text."%'";
				break;
            // Search by phone number or extension
            case 'phone':
				$query_text .= " Phone LIKE '%".$search_text."%'";
				break;
            // Search by first or last name
            case 'name':
			default:
                    $query_text .= " FirstName LIKE '%".$search_text."%'" .
                                   " OR LastName LIKE '%".$search_text."%'";
                    break;
		}
    }
	return $query_text;
}

// Open the SQLite database
$db = new SQLITE3('SQLiteDB/OfficeLayout.db', SQLITE3_OPEN_READONLY);
if(!$db)
{
    die('Database could not be accessed.');
}

// Grab the variables that were posted through AJAX.
$search_text = $_REQUEST['search_text'];
$filter = $_REQUEST['filter'];

// Initial SELECT query string without any filtering. This will be concatenated
// with filtering constraints if the user has selected any.
$query_text = "SELECT EmployeeID, CubicleNumber, FirstName, LastName, DeptName, Email, Phone FROM Personnel WHERE";

// Split the text in the search boxes by commas. 
// This is in case the user wishes to search for multiple items.
$exploded_string = explode(",", $search_text);
for($i = 0; $i < count($exploded_string); $i++)
{
    $query_text = generate_query($query_text, $db->escapeString($exploded_string[$i]), $filter);
    if($i < count($exploded_string) - 1)
    {
        $query_text .= " OR";
    }
}

// Query the database for all matches with our search constraints.
$statement = $db->prepare($query_text);
$results = $statement->execute();

$have_result = false;
// Display the search results by echoing back HTML to the AJAX/Javascript,
// which will display the results to the user in plain text.
while($results_row = $results->fetchArray(SQLITE3_ASSOC))
{
	$have_result = true;
    echo "<p id=\"search_result\"><strong>";
    echo $results_row['FirstName'];
    echo " ";
    echo $results_row['LastName'];
    echo "</strong><br />";
    echo "Department: ".$results_row['DeptName'];
    echo "<br />";
    echo "Cubicle #: ".$results_row['CubicleNumber'];
    echo "<br />";
    echo "E-mail: ".$results_row['Email'];
    echo "<br />";
    echo "Phone #: ".$results_row['Phone'];
    echo "<input type=\"hidden\" id=\"employeeID\" value=\"".$results_row['EmployeeID']."\">";
    echo "</p>";
    echo "<hr id=\"results_divider\" />";
}

if(!$have_result)
{
	echo '<p>Sorry. No results were found.</p>';
}

$results->finalize();
$statement->close();
$db->close();
?>
