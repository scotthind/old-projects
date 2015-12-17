<?php //Authors: Joseph Smith and Christopher Bowen
ini_set('display_errors', false);
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
if($row['userType'] != 'admin')
{   //If session not registered
    header("location:login.php?msg=You must be an admin to access this page."); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}

// Begin the process of adding a department

// If no object has been selected to be added...
if(!isset($_GET['objectType']))
{
    echo '<p>Error: No object to be added.</p>';
    sqlite_close($db);
    die();
            
}

if(!($_GET['objectType'] == "Department" && isset($_GET['departmentName']) && isset($_GET['iconID'])))
{
    echo '<p>Error: Please fill all fields.</p>';
    sqlite_close($db);
    die();
}

$insert_sql = "INSERT INTO Department (deptName, iconID) VALUES (\"". $_GET['departmentName']. "\",\"" . $_GET['iconID']."\")";
$insert= sqlite_exec($db, $insert_sql, $sqliteError);
if(!$insert)
{
    //if the insertion failed for any reason...
    sqlite_close($db);
    die($sqliteError);
}
echo '<p>'. $_GET['departmentName'].'<br/>Department added. Please refresh your browser before continuing.</p>';	
sqlite_close($db);
?>