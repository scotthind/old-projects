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

// Begin the process of editing the object

// If we don't know what we're editing, then we cannot proceed.
if(!isset($_GET['objectType']))
{
    echo '<p>Object to be edited cannot be determined.</p>';
}
// If we're editing a peripheral and we have the information we need...
elseif('Peripheral' == $_GET['objectType'] && isset($_GET['periphID'], $_GET['type'], $_GET['iconName']))
{   
    $query = 'UPDATE Peripherals SET Type=:type, IconID=:iconName WHERE PeriphID=:periphID';
    $statement = $db->prepare($query);
    $statement->bindParam(':type', $_GET['type'], SQLITE3_TEXT);
    $statement->bindParam(':iconName', $_GET['iconName'], SQLITE3_TEXT);
    $statement->bindValue(':periphID', intval($_GET['periphID']), SQLITE3_INTEGER);
	$result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query cannot be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>Peripheral edited.</p>';	
}
// If we're editing an employee and we have the information we need...
elseif('Employee' == $_GET['objectType'] && isset($_GET['EmployeeID'], $_GET['deptName'], $_GET['firstName'], $_GET['lastName'], $_GET['cubicle'])&& (isset($_GET['phone']) || isset($_GET['email'])))
{
	$phone = (isset($_GET['phone']) && $_GET['phone'] != null) ? $_GET['phone'] : null;
    $email = (isset($_GET['email']) && $_GET['email'] != null) ? $_GET['email'] : null;

	// Check that the person's name consists only of letters
	if(0 == preg_match('/[A-Za-z]/', $_GET['firstName']) || 0 == preg_match('/[A-Za-z]/', $_GET['lastName']))
	{
		die('Name is not a recognized legal variant.');
	}

	// Check that the e-mail is in a legitimate format
	if(!filter_var($_GET['email'], FILTER_VALIDATE_EMAIL))
	{
		die('Not a valid e-mail address.');
	}

	// a regex for matching 7 or 10 digit phone number with extensions allowed
	// courtesy of stackoverflow
	$phone_regex = "/^
		(?: (?: \( (?=\d{3}\)))?  (\d{3}) (?: (?<=\(\d{3}) \))?  [\s.\/-]?)?
		(\d{3}) [\s\.\/-]?  (\d{4})\s?  (?: (?: (?: (?:e|x|ex|ext)\.?  | extension) 
		\s?) (?=\d+) (\d+))?  $/x";

	// Check that the phone number is in a legitimate format
	if(0 == preg_match($phone_regex, $_GET['phone']))
	{
		die('Not a valid phone number.');
	}

    $query = 'UPDATE Personnel SET DeptName=:deptName, CubicleNumber=:cubicle, FirstName=:firstName, LastName=:lastName, Phone=:phone, Email=:email WHERE EmployeeID =:EmployeeID';
    $statement = $db->prepare($query);
    $statement->bindParam(':deptName', $_GET['deptName'], SQLITE3_TEXT);
    $statement->bindParam(':cubicle', $_GET['cubicle'], SQLITE3_TEXT);
    $statement->bindParam(':firstName', $_GET['firstName'], SQLITE3_TEXT);
    $statement->bindParam(':lastName', $_GET['lastName'], SQLITE3_TEXT);
    $statement->bindParam(':phone', $phone, ( ($phone != null) ? SQLITE3_TEXT : SQLITE3_NULL));
    $statement->bindParam(':email', $email, ( ($email != null) ? SQLITE3_TEXT : SQLITE3_NULL));
    $statement->bindParam(':EmployeeID', $_GET['EmployeeID'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query cannot be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>Employee edited.</p>';	
}
// Since this system handles only the editing of personnel and peripherals,
// getting here implies not an unrecognized object type, but a lack of other information
else
{
    echo '<p>Please fill all fields.</p>';
}
$db->close();
?>
