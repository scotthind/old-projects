<?php //Authors: Joseph Smith and Christopher Bowen

/**
 *This function attempts to verify a user's priveleges and connect them to
 * the database.
 * @param type $db_path The path to the database.
 * @param type $privelege The user's access rights.
 * @return SQLite3 A handle to the database if priveleges are sufficient or false otherwise.
 */
function connect($db_path, $privelege)
{
	// Check to see that all parameters have been set
	if(!(isset($db_path, $privelege) && is_string($db_path) && is_string($privelege)))
    {
        die('Invalid parameters.');
    }
    $dbh = new SQLite3($db_path);

	// Check that the database was accessed succesffuly
    if(!$dbh)
    {
        die('Database cannot be accessed.');
    }

	// Query the database for the permissions of the current user
    $statement = $dbh->prepare('SELECT UserType FROM User WHERE Username=:name');
	if(!$statement)
    {
        $dbh->close();
        die('Query not recognized.');
    }
    $statement->bindParam(':name', $_SESSION['name'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $dbh->close();
        die("Invalid query.");
    }

    $row = $result->fetchArray(SQLITE3_ASSOC);

	// If the user's account type does not have the priveleges required...
    if($row['UserType'] != $privelege)
    {   //If session not registered
		header("location:login.php?msg=You must have $privelege priveleges to access this page."); // Redirect to login.php page
		return false;
	}
	//Otherwise, we can grant permission to the current user
    else
    {   //Continue to current page
        header( 'Content-Type: text/html; charset=utf-8' );
	}
    $result->finalize();
    $statement->close();
    return $dbh;
}
?>
