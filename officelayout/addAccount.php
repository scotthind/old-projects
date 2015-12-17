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
		<a href="index.html"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a>
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

// Begin the process of adding a user account

// If we do not have all the information we need to die
if(!isset($_POST['employeeID'], $_POST['username'], $_POST['password'], $_POST['confirm_password'], $_POST['usertype']))
{
    echo "<p>One or more parameters were not found.</p>\n";
    $db->close();
    die();
}

// Get rid of white spaces
$_POST['employeeID'] = trim($_POST['employeeID']);
$_POST['username'] = trim($_POST['username']);
$_POST['password'] = trim($_POST['password']);
$_POST['confirm_password'] = trim($_POST['confirm_password']);
$_POST['usertype'] = trim($_POST['usertype']);

// If any elements to be input are set to the empty string display an error message
if($_POST['employeeID'] == '' || $_POST['username'] == '' || $_POST['password'] == '' || $_POST['usertype'] == '')
{
    echo "<p><strong>Error: Please fill all fields.</strong></p>\n<br/>\n";
	echo "<p><strong>Please go back and try again.</strong></p>\n";
}
// If the passwords do not match display an error message
elseif($_POST['password'] != $_POST['confirm_password'])
{
    echo "<p><strong>Error: The password did not match its confirmation.</strong></p>\n";
	echo "<p><strong>Please go back and try again.</strong></p>\n";
}
// If everything checks out, we can proceed to try and insert into the database.
else
{
    $query = 'INSERT INTO User (Username, Password, UserType, EmployeeID) VALUES (:username, :password, :usertype, :employeeID)';
    $statement = $db->prepare($query);
    $statement->bindParam(':username', $_POST['username'], SQLITE3_TEXT);
    $statement->bindParam(':password', $_POST['password'], SQLITE3_TEXT);
    $statement->bindParam(':usertype', $_POST['usertype'], SQLITE3_TEXT);
    $statement->bindParam(':employeeID', $_POST['employeeID'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();
    echo "<p><strong>User added successfully.</strong></p>\n";
}
$db->close();
?>
		<div id="gohome">
			<!-- Link to go back to the account management page -->
			<a href="accounts.php"
				title="Click here to return to the accounts page">Back to Manage Accounts</a>
			<!-- Space out the links to make it look cleaner -->
			&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
            <!-- Link to go back to the admin page -->
            <a href="admin.php"
                title="Click here to go back to the admin page">Back to Admin Page</a>
        </div>
    </div>
</div>
</body>
</html>
