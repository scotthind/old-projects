<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="Author" content="Joseph Smith and Christopher Bowen" />
<title>Human Resources - Confirmation</title>
<link rel="shortcut icon" href="img/mse.jpg" />
<link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
<link rel="stylesheet" href="css/userStyle.css" media="screen" type="text/css" />
<!--[if IE]><link rel="stylesheet" href="css/IEstyle.css" media="screen" type="text/css" /><![endif]-->
<!--[if IE 7]><link rel="stylesheet" href="css/IE7style.css" media="screen" type="text/css" /><![endif]-->
</head>

<body>
<div id ="maincontainer">
    <div id="loginlogo">
        <a href="admin.php"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a>
    </div>
    <div class="clear"></div>
    <div id="confirmationsection">

<?php //Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
ini_set('display_errors', false);
session_start(); //Start the session


// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
if(null == $db || !$db)
{
    die();
}

// Closes of the HTML so the page can retain its appearance
$closing = "\n<div id=\"gohome\"> \n <a href=\"accounts.php\" \n" . 
		   "title=\"Click here to return to the Account Management page\">Back to Account Management</a>\n" .
           "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n" .
           "<a href=\"admin.php\"\n title=\"Click here to go back to the admin page\">Back to Admin Page</a>\n" .
		   "</div>\n </div>\n </div>\n </body>\n </html>";

// Begin the process of removing an account
if(!isset($_POST['username']))
{
    echo '<p><strong>Cannot locate username.</strong></p><br />' . $closing;
    $db->close();
    die();
}

// Get rid of whitespace.
$_POST['username'] = trim($_POST['username']);

// If the username is the empty string...
if($_POST['username'] == '')
{
    $db->close();
    echo '<p><strong>Please enter a username.</strong></p><br />' . $closing;
    die();
}

// Counts the number of people with the requested user name
$query = 'SELECT count(*) as count FROM User WHERE Username = :username';
$statement = $db->prepare($query);
$statement->bindParam(':username', $_POST['username'], SQLITE3_TEXT);
$result = $statement->execute();
if(!result)
{
    $statement->close();
    $db->close();
    die('Query could not be executed.');
}

// Fetches the result of the previous query
$row = $result->fetchArray(SQLITE3_ASSOC);
if($row['count'] == 0)
{
    echo '<p><strong>User account does not exist.</strong></p><br />' . $closing;
    $result->finalize();
	$statement->close();
    $db->close();
    die();
}
$result->finalize();
$statement->close();

//Otherwise, proceed to delete the account.
$query = 'DELETE FROM User WHERE Username = :username';
$statement = $db->prepare($query);
$statement->bindParam(':username', $_POST['username'], SQLITE3_TEXT);
$result = $statement->execute();
if(!$result)
{
    $statement->close();
    $db->close();
    die('Query could not be executed.');
}
echo '<p><strong>User account removed.</strong></p><br />' . $closing;
$result->finalize();
$statement->close();
$db->close();
?> 
