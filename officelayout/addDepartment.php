<?php //Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
ini_set('display_errors', false);
session_start(); //Start the session


// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
if(null == $db || !$db)
{
    die();
}

// Begin the process of adding a department

// If no object has been selected to be added...
if(!isset($_GET['objectType']))
{
    echo '<p>Error: No object to be added.</p>';
    $db->close();
    die();
}

//Check to see that all parameters have been set appropriately
if(!($_GET['objectType'] == "Department" && isset($_GET['departmentName'], $_GET['iconID']) && is_string($_GET['departmentName'])))
{
    echo '<p>Error: Please fill all fields.</p>';
    $db->close();
    die();
}

//Trim away any extraneous white space
$_GET['departmentName'] = trim($_GET['departmentName']);
$_GET['iconID'] = trim($_GET['iconID']);

//Construct the insertion query
$query = 'INSERT INTO Department (DeptName, IconID) VALUES (:deptName, :iconID)';
$statement = $db->prepare($query);
$statement->bindParam(':deptName', $_GET['departmentName'], SQLITE3_TEXT);
$statement->bindParam(':iconID', $_GET['iconID'], SQLITE3_TEXT);
$result = $statement->execute();
if(!$result)
{
    //if the insertion failed for any reason...
    $statement->close();
    $db->close();
    die('Query could not be executed.');
}
echo '<p>Department added.</p>';
$result->finalize();
$statement->close();
$db->close();
?>
