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
// whatever calls insert now needs to pass employeeID and no longer passes long, lat or floor

// Begin the process of inserting the object

// If the type of object to be added has not been set, then we cannot insert.
if(!isset($_GET['objectType']))
{
    echo '<p>Object to be inserted cannot be determined.</p>'; 
}
// If we are adding an employee and have the information we need...
elseif('Employee' == $_GET['objectType'] && isset($_GET['employeeID'], $_GET['cubeNumber'], $_GET['firstName'], $_GET['lastName']) && (isset($_GET['phone']) || isset($_GET['email'])))
{
    $phone = (isset($_GET['phone']) && $_GET['phone'] != '') ? $_GET['phone'] : null;
    $email = (isset($_GET['email']) && $_GET['email'] != '') ? $_GET['email'] : null;
  
	// Check that the person's name consists only of letters
    if(0 == preg_match('/[A-Za-z]/', $_GET['firstName']) || 0 == preg_match('/[A-Za-z]/', $_GET['lastName']))
    {
        die('Name is not a recognized legal variant.');
    }

    // check that the email is in a legitimate format
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

    // check that the phone number in a legitimate format
    if(0 == preg_match($phone_regex, $_GET['phone']))
    {
        die('Not a valid phone number.');
    }

    $query = 'INSERT INTO Personnel (EmployeeID, FirstName, LastName, DeptName, Email, Phone, CubicleNumber) '.
                'VALUES(:employeeID, :firstName, :lastName, :deptName, :email, :phone, :cubeNumber)';
    $statement = $db->prepare($query);
    $statement->bindParam(':employeeID', $_GET['employeeID'], SQLITE3_TEXT);
    $statement->bindParam(':firstName', $_GET['firstName'], SQLITE3_TEXT);
    $statement->bindParam(':lastName', $_GET['lastName'], SQLITE3_TEXT);
    $statement->bindParam(':deptName', $_GET['deptName'], SQLITE3_TEXT);
    $statement->bindParam(':email', $email, ( ($email != null) ? SQLITE3_TEXT : SQLITE3_NULL));
    $statement->bindParam(':phone', $phone, ( ($phone != null) ? SQLITE3_TEXT : SQLITE3_NULL));
    $statement->bindParam(':cubeNumber', $_GET['cubeNumber'], SQLITE3_TEXT);
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>Employee added.</p>';
}
// If we are adding a peripheral and have the information we need
elseif('Peripheral' == $_GET['objectType'] && isset($_GET['type'], $_GET['floor'], $_GET['lat'], $_GET['long'], $_GET['iconName']))
{
	$query = 'INSERT INTO Peripherals (Type, Floor, IconID, Longitude, Latitude) ' .
                'VALUES(:type, :floor, :iconName, :longitude, :latitude)';
    $statement = $db->prepare($query);
	$statement->bindParam(':type', $_GET['type'], SQLITE3_TEXT);
    $statement->bindValue(':floor', intval($_GET['floor']), SQLITE3_INTEGER);
    $statement->bindParam(':iconName', $_GET['iconName'], SQLITE3_TEXT);
    $statement->bindValue(':longitude', floatval($_GET['long']), SQLITE3_FLOAT);
    $statement->bindValue(':latitude', floatval($_GET['lat']), SQLITE3_FLOAT);
    $result = $statement->execute();

    if(!$result)
    {
        $statement->close();
		$db->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>Peripheral added.</p>';
}
// Since this system handles only the insertions of personnel and peripherals,
// getting here implies not an unrecognized object type, but a lack of other information
else
{
    echo '<p>Please fill all fields.</p>';
}
$db->close();
?>
