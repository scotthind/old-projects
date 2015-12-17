<?php //Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
if(null == $db || !$db)
{
    die();
}

// Begin the process of updating the object

// If the sort of thing to be updated has not been set, then we cannot proceed.
if(!isset($_GET['objectType']))
{
    echo '<p>Object to be updated cannot be determined.</p>';
}
// If we are updating a peripheral and we have all necessary information...
else if('Peripheral' == $_GET['objectType'] && isset($_GET['periphID'], $_GET['lat'], $_GET['long']))
{
	$query = 'UPDATE Peripherals SET Latitude=:lat, Longitude=:long WHERE PeriphID =:periphID';
    $statement = $db->prepare($query);
    $statement->bindValue(':lat', floatval($_GET['lat']), SQLITE3_FLOAT);
    $statement->bindValue(':long', floatval($_GET['long']), SQLITE3_FLOAT);
    $statement->bindParam(':periphID', $_GET['periphID'], SQLITE3_INTEGER);
    $result = $statement->execute();

    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Query could not be executed.');
    }
    $result->finalize();
    $statement->close();
    echo '<p>Peripheral updated.</p>';		
}
// Otherwise, we do not have enough information to proceed
else
{
    echo '<p>Please fill all fields.</p>';
}
$db->close();
?>
