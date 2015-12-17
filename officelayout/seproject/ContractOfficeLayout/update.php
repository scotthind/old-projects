<?php //Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
if(false == $result)
{
    sqlite_close($db);
    die("Database error: " . $sqlite_error);
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

// Begin the process of updating the object

if(!isset($_GET['objectType']))
{
    echo 'Error! Please fill all fileds!';
}
else if($_GET['objectType'] == "Utility" && isset($_GET['utilID']) && isset($_GET['lat'])&& isset($_GET['long']))
{
    $utilID = $_GET['utilID'];
    $lat = $_GET['lat'];
    $long = $_GET['long'];
    $update_sql = "UPDATE Utilities SET lat = " . $lat . ", long = ". $long . " WHERE utilID = " . $utilID;
    $result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
    if(false == $result)
    {
        sqlite_close($db);
        die("Database error: " . $sqlite_error);
    }
    echo 'Went through clean<br/>Please refresh your browser, before continuing';		
}
elseif($_GET['objectType'] == "Person" && isset($_GET['personID']) && isset($_GET['lat']) && isset($_GET['long']))
{
    $personID = $_GET['personID'];
    $lat = $_GET['lat'];
    $long = $_GET['long'];
    $update_sql = "UPDATE cubicle SET loclat = " . $lat . ", loclong = ". $long . " WHERE personID = " . $personID;
    $result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
    if(false == $result)
    {
        sqlite_close($db);
        die("Database error: " . $sqlite_error);
    }
    echo 'Went through clean';
}
else
{
    echo 'PROBLEM!';
}
sqlite_close($db);
?>