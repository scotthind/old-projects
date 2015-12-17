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

<?php // Authors: Joseph Smith & Christopher Bowen
require_once 'connectDB.php';
session_start();

// Validate form input

//Check that all required information has been provided
$error_message = '';
if(!isset($_POST['employee_id'], $_POST['cubicleNum'], $_POST['first_name'], $_POST['last_name']) || 
	(!isset($_POST['telephone']) && (!isset($_POST['email']))))
{
    $error_message .= "Employee information invalid.<br />\n";
}

// Check that the person's name consists only of letters
if(0 == preg_match('/[A-Za-z]/', $_POST['first_name']) || 0 == preg_match('/[A-Za-z]/', $_POST['last_name']))
{
    $error_message .= "Name is not a recognized legal variant.<br />\n";
}

// If the phone number was submitted, check that it is in a legitimate format
if(isset($_POST['telephone']))
{
   // a regex for matching 7 or 10 digit phone number with extensions allowed
   // courtesy of stackoverflow
   $phone_regex = "/^
       (?: (?: \( (?=\d{3}\)))?  (\d{3}) (?: (?<=\(\d{3}) \))?  [\s.\/-]?)?
	   (\d{3}) [\s\.\/-]?  (\d{4})\s?  (?: (?: (?: (?:e|x|ex|ext)\.?  | extension)
	   \s?) (?=\d+) (\d+))?  $/x";
   
	if(0 == preg_match($phone_regex, $_POST['telephone']))
   {
       $error_message .= "Not a valid phone number.<br />\n";
   }
}

// If the e-mail address was submitted, check that it is in a legitimate format
if(isset($_POST['email']))
{
	if(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL))
	{
		$error_message .= "Not a valid e-mail address.<br />\n";
	}
}

// If an error has occured, we still want the links to display on the error page
if($error_message != '')
{
	echo "<div id=\"gohome\">\n<p id=\"errorMessage\">\n<strong>The following errors have occurred:</strong><br /> " . $error_message . "</p>\n" . 
		 "<a href=\"hr.php\" \n title=\"Click here to return to the HR page\">Please go back and try again.</a>\n</div>\n"; 
    die();
}

//Connect to the database
$db = connect('SQLiteDB/OfficeLayout.db', 'humanr');
if(null == $db || !$db)
{
    die();
}

//Generate e-mail message

// Fetch the current user's e-mail address.
$query = "SELECT Email FROM User natural join Personnel WHERE Username=:name";
$statement = $db->prepare($query);
$statement->bindParam(':name', $_SESSION['name'], SQLITE3_TEXT);
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


//Build header
$headers = "From: {$row['Email']} \r\n".
"Reply-To: {$row['Email']} \r\n".
'X-Mailer: PHP/' . phpversion(); 

//Email subject
$email_subject = "Personnel Addition";

// E-mail message

$query = "SELECT count(*) as count FROM Personnel WHERE EmployeeID=:eid";
$statement = $db->prepare($query);
$statement->bindParam(':eid', $_POST['employee_id'], SQLITE3_TEXT);

//Determine the type of request (i.e., addition of personnel vs. change of information)
$result = $statement->execute();
if(!$result)
{
    $statement->close();
    $db->close();
    die('Query could not be executed.');	
}
$row = $result->fetchArray(SQLITE3_ASSOC);
if(0 == $row['count'])
{
    $email_message = "It has been requested that the following employee be entered into the system:\n";
}
else if(1 == $row['count'])
{
    $email_message = "It has been requested that the following person's information be changed to the following:\n";
}
else
{   // This should never happen unless something is inconsistent between the PHP and the database
    echo "<p>There was more than one matching individual.</p>";
    $db->close();
    die();
}
$result->finalize();
$statement->close();

$telephone = $_POST['telephone']; // not required
$email = $_POST['email']; // required
$comments = $_POST['comments']; // required


// Populate the e-mail with the employee's information
$email_message .= "EmployeeID: " . $_POST['employee_id'] . "\n" .
                  "Cubicle Number: " . $_POST['cubicleNum'] . "\n" .
                  "First Name: " . $_POST['first_name'] . "\n" .
                  "Last Name: " . $_POST['last_name'] . "\n" .
                  "Telephone: " . $telephone . "\n" .
                  "E-mail: " . $email . "\n" .
                  "Comments: " . $comments . "\n";

// Query the database for the e-mail addresses of all administrators
$query="SELECT distinct Email " .
       "FROM User natural join Personnel " . 
       "WHERE UserType = 'admin' AND Email IS NOT NULL";
$statement = $db->prepare($query);
$result = $statement->execute();
if(!$result)
{
    $statement->close();
    $db->close();
    die('Query could not be executed.');	
}

while($row = $result->fetchArray(SQLITE3_ASSOC))
{  
    @mail($row['Email'], $email_subject, $email_message, $headers);  
}
$result->finalize();
$statement->close();
$db->close();
?>

		<h3>Request Submitted</h3>
		<br />
		<!-- Confirmation Message -->
		<p>Thank you for submitting the personnel change. 
			The administration staff will apply the change after they
			receive this message.</p>
		<br />
		<div id="gohome">
			<!-- Link to go back to the main page -->
			<a href="index.html"
                title="Click here to go back to the main page">Back to Office Layout Home </a>
			<!-- Space out the links to make it look cleaner -->
            &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
			<!-- Link to go back to the HR page -->
            <a href="hr.php"
                title="Click here to go back to the HR page">Human Resources Page</a>
        </div>
	</div>
</div>
</body>
</html>
