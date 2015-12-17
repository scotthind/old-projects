//Authors: Joseph Smith and Christopher Bowen

<?php
ini_set('display_errors', false);
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
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

// If an object has been selected to be added...
if(isset($_GET['objectType']))
{
    // If that object is of type department and the necessary values are set...
    if($_GET['objectType'] == "Department" && isset($_GET['departmentName']) && isset($_GET['iconID']))
    {
        $insert_sql = "INSERT INTO Department (deptName, iconID) VALUES (\"". $_GET['departmentName']. "\",\"" . $_GET['iconID']."\")";
        $insert= sqlite_exec($db, $insert_sql, $sqliteError);
        if(!$insert)
        {
            //if the insertion failed for any reason...
            sqlite_close($db);
            die($sqliteError);
        }
        echo '<p>'. $_GET['departmentName'].'<br/>Department added. Please refresh your browser before continuing.</p>';	
    }
    // Otherwise, we cannot add a department
    else
    {
        echo '<p>Error: Please fill all fields.</p>';
    }
}
// Otherwise, there was no object to be added
else
{
    echo '<p>Error: No object to be added.</p>';
}
sqlite_close($db);
?>