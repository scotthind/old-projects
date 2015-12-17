<?php // Authors: Joseph Smith & Christopher Bowen
// Set up resources
$username = $_POST['username']; //Set UserName
$password = $_POST['password']; //Set Password
$msg ="";

// If the username and password are not set, then we exit.
if(!isset($username, $password))
{
    header("location:login.php?msg=Please enter a username and password.");
    die();
}

$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);

// Query the database for a user with the specified name and password
$sql="SELECT userType FROM user WHERE username = \"".$username."\" and password = \"".$password."\"";
$result = sqlite_query($db, $sql);
if(false == $result)
{
    sqlite_close($db);
    die("Database error: " . $sqlite_error);	
}

// Determine the number of matches
$count= sqlite_num_rows($result);

// If result matched one $username and $password pair, $count must be 1
if(1 == $count)
{
    $row = @sqlite_fetch_array($result, SQLITE_ASSOC);
    // Register $username, $password and redirect to "admin.php"
    if($row['userType'] == "admin")
    {
        session_register("admin");
        $_SESSION['name']= $username;
        header("location:admin.php");
    }
    // Register $username, $password and redirect to "hr.php"
    elseif($row['userType'] == "humanr")
    {
        session_register("humanr");
        $_SESSION['name']= $username;
        header("location:hr.php");
    }
    else
    {   // If we get here, there is a problem with the database
        $msg = "Database inconsistency. User has unrecognized type.";
        header("location:login.php?msg=$msg");
    }
}
// If the result did not match any users, $count must be 0
else if(0 == $count)
{
    $msg = "Wrong Username or Password. Please try again.";
    header("location:login.php?msg=$msg");
}
else
{   
    // If we get here, there is a problem with the database
    $msg = "Database inconsistency. More than one user with username and password.";
    header("location:login.php?msg=$msg");
}
sqlite_close($db);
?>