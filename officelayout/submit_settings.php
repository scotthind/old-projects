<?php //Authors: Joseph Smith and Christopher Bowen
ini_set('display_errors', false);
session_start(); //Start the session

// Begin handling the submitted change requests

header( 'Content-Type: text/html; charset=utf-8' );
$db = new SQLITE3('SQLiteDB/OfficeLayout.db');
if(!$db)
{
    die('Could not access database.');
}

$msg = "";

// If there was a request was to change the user's password, then...
if(isset($_POST['new_pass'], $_POST['confirm_pass']) && $_POST['new_pass'] != "")
{	
    // Note that the user must have already entered their password to get here
    // hence, there is no need to ask them to enter their old one again
    if($_POST['new_pass'] == $_POST['confirm_pass']) 
    {
        $query = "UPDATE User SET Password=:newpass WHERE Username=:name";
        $statement = $db->prepare($query);
        $statement->bindParam(':newpass', $_POST['new_pass'], SQLITE3_TEXT);
        $statement->bindParam(':name', $_SESSION['name'], SQLITE3_TEXT);
        $result = $statement->execute();
        if(!$result)
        {           
            $statement->close();    
            $db->close();
            die('Query could not be executed.');
        }
        $result->finalize();
        $statement->close();
		$msg .= "Password was changed successfully!";	
    } 
    else 
    {
        $msg .= "Error: The new password and the password confirmation entered did not match.";
    }
}
// If there was a request to change the user's e-mail, then...
if(isset($_POST['email']) && $_POST['email'] != "")
{
	if(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL))
	{
		$msg .= "Error: The new e-mail address is not valid.";
	}
    else
    {
        $query = "UPDATE Personnel SET Email=:email WHERE EmployeeID in (SELECT EmployeeID FROM User WHERE Username=:name)";
        $statement = $db->prepare($query);
        $statement->bindParam(':email', $_POST['email'], SQLITE3_TEXT);
        $statement->bindParam(':name', $_SESSION['name'], SQLITE3_TEXT);
        $result = $statement->execute();
		if(!$result)
        {
            $statement->close();
            $db->close();
            die('Query could not be executed.');
        }
        $result->finalize();
        $statement->close();
        $msg .= "E-mail address was changed successfully!";	
    }
}

if('' == $msg)
{
	$msg = "No request was made.";
}

header("location:settings.php?msg=".$msg);
$db->close();
?>
