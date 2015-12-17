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

// Begin the process of editing the object

if(isset($_GET['objectType']))
{
    if($_GET['objectType'] == "Utility" && isset($_GET['utilID']) && isset($_GET['type'])&& isset($_GET['iconName']))
    {
        $utilID = $_GET['utilID'];
	$type = $_GET['type'];
	$iconName = $_GET['iconName'];
	$update_sql = "UPDATE Utilities SET type = \"" . $type . "\", iconID = \"".$iconName."\" WHERE utilID = " . $utilID;
	$result = sqlite_exec($db, $update_sql, $sqliteError);
	if(false == $result)
        {
            die($sqliteError . ", please try again");
	}
	sqlite_close($db);
	echo 'Object edited: Went through clean<br/>Please refresh your browser, before continuing';	
    }
    elseif($_GET['objectType'] == "Person" && isset($_GET['personID']) && isset($_GET['deptName'])&& isset($_GET['firstName'])&& isset($_GET['lastName'])&& isset($_GET['phone'])&& isset($_GET['email'])&& isset($_GET['cubicle']))
    {
        $personID = $_GET['personID'];
	$deptName = $_GET['deptName'];
	$firstName = $_GET['firstName'];
	$lastName = $_GET['lastName'];
	$phone = $_GET['phone'];
	$email = $_GET['email'];
	$cubicle = $_GET['cubicle'];
		
	$update_sql = "UPDATE Personnel SET deptName = \"" . $deptName . "\", firstName = \"". $firstName . "\", lastName = \"" . $lastName .
		      "\", phone = \"" .$phone."\", email = \"" .$email."\" WHERE personID = " . $personID;
	$result = sqlite_exec($db, $update_sql, $sqliteError);
	if(false == $result)
        {
            die($sqliteError . ", please try again");
	}
        
	$update_sql = "UPDATE cubicle SET cubeNumber = \"" . $cubicle . "\" WHERE personID = " . $personID;
	$result = sqlite_exec($db, $update_sql, $sqliteError);
	if(false == $result)
        {
            die($sqliteError . ", please try again");
	}
        
	sqlite_close($db);
        echo 'Object edited : Went through clean<br/>Please refresh your browser, before continuing';	
    }
	
}
else
{
    echo 'Error! Please fill all fileds!';
    sqlite_close($db);
}
?>