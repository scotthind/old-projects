<?php //Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
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

// Begin the process of removing a department

if(!(isset($_GET['departmentName'])  && isset($_GET['sure'])))
{
    sqlite_close($db);
    echo 'Department and sureness value are both required.';
    die();
}

// If the user selected the 'No Department' option
// (representing all individuals without a department),
// then
if('No Department' == $_GET['departmentName'])
{
    echo "<p>Individuals without a department must be removed individually.</p>";
    sqlite_close($db);
    die();
}	

// This will occur when the "Remove Department" button is toggled
if('false' == $_GET['sure'])
{
    $query = "SELECT count(*) FROM Personnel WHERE deptName = \"". $_GET['departmentName']."\"" ;
    $result = sqlite_query($db, $query);
    if(!$result)
    {
        sqlite_close($db);
        die("Invalid query: " . sqlite_error());
    }
    $row = @sqlite_fetch_array($result, SQLITE_ASSOC);
    echo $row['count(*)'] . ' employees will left without a department. Continue?';		
}

// This will occur if the user proceeds to choose a department and remove it
elseif('true' == $_GET['sure'])
{
    // First update all of the personnel belonging to the department to be removed so that they are
    // listed as being in No Department - we will violate foreign key constraints if we remove the department
    // first
    $update_sql = "UPDATE Personnel SET deptName = \"No Department\" WHERE deptName = \"".$_GET['departmentName']."\"";
    $result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
    if(false == $result)
    {
        sqlite_close($db);
        die("Database error: " . $sqlite_error);
    }

    // Having updated the Personnel table, proceed to remove the department 
    $delete_sql = "DELETE FROM Department WHERE deptName = \"".$_GET['departmentName']."\"";
    $result = sqlite_query($db, $delete_sql, SQLITE_ASSOC, $sqlite_error);
    if(false == $result)
    {
        sqlite_close($db);
        die("Database error: " . $sqlite_error);	
    }
    echo "<p>".$_GET['departmentName']." has been removed.<br/>Please refresh your browser.</p>";
}
else
{
    // We should never reach this point
    echo '<p> Could not obtain truth value for user <br /> while attempting to remove'.$_GET['departmentName'].'</p>';
}

sqlite_close($db);
?>