//Authors: Joseph Smith and Christopher Bowen

<?php
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

// Begin the process of removing the object

if(isset($_GET['objectType']))
{
    if($_GET['objectType'] == "Utility" && isset($_GET['utilID']))
    {
        $utilID = $_GET['utilID'];
	$update_sql = "DELETE from Utilities WHERE utilID = " . $utilID;
	$result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
	if (false == $result)
        {
		die("SQLite error: " . $sqlite_error);
	}
	sqlite_close($db);
	echo 'Went through clean<br/>Please refresh your browser, before continuing';	
    }
    elseif($_GET['objectType'] == "Person" && isset($_GET['personID']))
    {
        $personID = $_GET['personID'];
	$update_sql = "DELETE from cubicle WHERE personID = " . $personID;
	$result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
	if (false == $result) 
        {
            die("SQLite error: " . $sqlite_error);
	}
	$update_sql = "DELETE from Personnel WHERE personID = " . $personID;
		
	$result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
	if (false == $result)
        {
            die("SQLite error: " . $sqlite_error);
	}
	sqlite_close($db);
	echo 'Went through clean';
    }
    else
    {
	echo 'PROBLEM !';
	sqlite_close($db);
    }
}
else{
	echo 'Error! Please fill all fields!';
	sqlite_close($db);
}
?>