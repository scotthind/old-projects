<?php // Authors: Joseph Smith & Christopher Bowen

// Set up resources
$username = $_POST['username'];
$password = $_POST['password'];
$msg ="";

// If the username and password are not set or are not strings, then we exit.
if(!isset($username, $password) && is_string($username) && is_string( $password))
{
    header("location:login.php?msg=Please enter a username and password.");
    die();
}

//Attempt to connect to the database
$db = new SQLite3('SQLiteDB/OfficeLayout.db', SQLITE3_OPEN_READONLY);
if(!$db)
{
    die('Could not access database.');
}

// Query the database for a user with the specified name and password
$query = 'SELECT UserType FROM User WHERE Username=:username AND Password =:password';
$statement = $db->prepare($query);
$statement->bindParam(':username', $username, SQLITE3_TEXT);
$statement->bindParam(':password', $password, SQLITE3_TEXT);
$result = $statement->execute();
if(!$result)
{
    $statement->close();
    $db->close();
    die('Query could not be executed.');	
}

$row = $result->fetchArray(SQLITE3_ASSOC);
$result->finalize();
$statement->close();
//If there were no results...
if(!$row)
{
    $db->close();
    $msg = "Wrong username or password. Please try again.";
    header("location:login.php?msg=$msg");
    die();
}
//Begin a session for this user
session_start();
$_SESSION['name'] = $username;
header($row['UserType'] == "admin" ? "location:admin.php" : "location:hr.php");
$db->close();
?>
