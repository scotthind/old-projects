<?php //Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
if(!$db)
{
    die($sqliteerror);
}
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
if(!$result)
{
    sqlite_close($db);
    die("Invalid query: " . sqlite_error());
}

$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
if('admin' != $row['userType'])
{   //If session not registered
    header("location:login.php?msg=You must be an admin to access this page."); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}

// Begin the process of removing the object

// Deletes an entry from a table in the database.
// Assumes that the thing to be deleted has a single attribute
// that can identify it (single-attribute primary key)
// $what -- The sort of thing we are deleting
// $from -- The name of the table we are deleting a $what from
// $where -- The attribute we are checking against in the $from table
// $id -- The value we are looking for in the $where column of the $from table
// $database -- The database we are deleting from
function delete($what, $from, $where, $id, $database)
{
    $delete_sql = "DELETE FROM " . $from . " WHERE " . $where . " = \"" . $id . "\"";
    if(!sqlite_query($database, $delete_sql, SQLITE_ASSOC, $sqliteerror))
    {
        sqlite_close($database);
        die("Database error: " . $sqliteerror);
    }
    echo '<p>' . $what . ' removed.</p>';
}

// If the sort of thing to be deleted has not been set, then we cannot proceed.
if(!isset($_GET['objectType']))
{
    	echo '<p>No object to remove.</p>';
}
// If we are deleting a peripheral and we have the id...
elseif('Peripheral' == $_GET['objectType'] && isset($_GET['periphID']))
{
    delete('Peripheral', 'Peripherals', 'periphID', $_GET['periphID'], $db);
}
// If we are deleting an employee and we have the id...
elseif('Employee' == $_GET['objectType'] && isset($_GET['EmployeeID']))
{
    delete('Employee', 'Personnel', 'EmployeeID', $_GET['EmployeeID'], $db);
}
// Since this system handles only the deletions of personnel and peripherals,
// getting here implies not an unrecognized object type, but a lack of id
else
{
    echo '<p> Please fill all fields.</p>';
}
sqlite_close($db);
?>