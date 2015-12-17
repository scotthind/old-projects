<?php //Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
if(null == $db || !$db)
{
    die();
}

// Begin the process of removing the object

/** Deletes an entry from a table in the database.
  * Assumes that the thing to be deleted has a single attribute
  * that can identify it (single-attribute primary key)
  * @param $what -- The sort of thing we are deleting
  * @param $from -- The name of the table we are deleting a $what from
  * @param $where -- The attribute we are checking against in the $from table
  * @param $id -- The value we are looking for in the $where column of the $from table - assumed to be a string or integer
  * @param $database -- The database we are deleting from
  */
function delete($what, $from, $where, $id, $database)
{
    $query = "DELETE FROM $from WHERE $where =:id";

    $statement = $database->prepare($query);
    $statement->bindParam(':id', $id, (is_string($id) ? SQLITE3_TEXT :SQLITE3_INTEGER));
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $database->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>' . $what . ' removed.</p>';
}

// If the sort of thing to be deleted has not been set, then we cannot proceed.
if(!isset($_GET['objectType']))
{
    	echo '<p>Object to be removed cannot be determined.</p>';
}
// If we are deleting a peripheral and we have the id...
elseif('Peripheral' == $_GET['objectType'] && isset($_GET['periphID']))
{
    delete('Peripheral', 'Peripherals', 'PeriphID', intval($_GET['periphID']), $db);
}
// If we are deleting an employee and we have the id...
elseif('Employee' == $_GET['objectType'] && isset($_GET['EmployeeID']))
{
    delete('Employee', 'Personnel', 'EmployeeID', $_GET['EmployeeID'], $db);
}
// Since this system handles only the deletions of personnel and peripherals,
// getting here implies not an unrecognized object type, but a lack of other information
else
{
    echo '<p>Please fill all fields.</p>';
}
$db->close();
?>
