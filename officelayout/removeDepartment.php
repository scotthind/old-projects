<?php //Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
session_start(); //Start the session


// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
if(null == $db || !$db)
{
    die();
}

// Begin the process of removing a department

if(!isset($_GET['departmentName'], $_GET['sure']))
{
    $db->close();
    echo '<p>Department and sureness value are both required.</p>';
    die();
}

// If the user selected the 'No Department' option
// (representing all individuals without a department),
// then
if('No Department' == $_GET['departmentName'])
{
    echo '<p>Individuals without a department must be removed individually.</p>';
    $db->close();
    die();
}	

// This will occur when the "Remove Department" button is toggled
// Used to inform the admin how many (if any) people with be left
// without departments
if('0' == $_GET['sure'])
{
    $query = 'SELECT count(*) FROM Personnel WHERE DeptName = :departmentName';
    $statement = $db->prepare($query);
    $statement->bindParam(':departmentName', $_GET['departmentName'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');
    }
    $row = $result->fetchArray(SQLITE3_ASSOC);
    echo '<p>'.$row['count(*)'] . ' employees will be left without a department. Continue?</p>';
    $result->finalize();
    $statement->close();
}

// This will occur if the user proceeds to choose a department and remove it
elseif('1' == $_GET['sure'])
{
    // First update all of the personnel belonging to the department to be removed so that they are
    // listed as being in No Department - we will violate foreign key constraints if we remove the department
    // first
    $query = 'UPDATE Personnel SET DeptName = \'No Department\' WHERE DeptName = :departmentName';
	$statement = $db->prepare($query);
    $statement->bindParam(':departmentName', $_GET['departmentName'], SQLITE3_TEXT);
    $result = $statement->execute();

    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();

    // Having updated the Personnel table, proceed to remove the department 
    $query = 'DELETE FROM Department WHERE DeptName = :departmentName';
    $statement = $db->prepare($query);
    $statement->bindParam(':departmentName', $_GET['departmentName'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');	
    }
    $result->finalize();
    $statement->close();
    echo "<p>".$_GET['departmentName']." has been removed.</p>";
}
elseif('2' == $_GET['sure'])
{
	echo '<p>'.$_GET['departmentName'].' will not be removed.</p>';
}
else
{
    // We should never reach this point
    echo '<p> Could not obtain truth value for user <br /> while attempting to remove '.$_GET['departmentName'].'.</p>';
}
$db->close();
?>
