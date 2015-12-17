<?php //Authors: Joseph Smith and Christopher Bowen
ini_set('display_errors', false);
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
if(!$db)
{
    die($sqliteerror);
}
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
if(!$result)
{
    sqlite_close($db);
    die("Invalid query: " . sqlite_error());
}

$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
if('admin' != $row['userType'])
{   //If session not registered
    header("location:login.php?msg=You must be an admin to access this page."); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}

// whatever calls insert now needs to pass employeeID and no longer passes long, lat or floor

// Begin the process of inserting the object

// If the type of object to be added has not been set, then we cannot insert.
if(!isset($_GET['objectType']))
{
    echo 'Error! Please fill all fields!';
    sqlite_close($db);   
}
// If we are adding an employee and have the information we need...
elseif('Employee' == $_GET['objectType'] && isset($_GET['employeeID']) && isset($_GET['cubeNumber']) && isset($_GET['firstName'])&& isset($_GET['lastName']) && (isset($_GET['phone']) || isset($_GET['email'])))
{
    $phone = isset($_GET['phone']) ? $_GET['phone'] : null;
    $email = isset($_GET['email']) ? $_GET['email'] : null;
    
    $insert_sql = 'INSERT INTO Personnel (EmployeeID, FirstName, LastName, DeptName, Email, Phone, CubicleNumber) 
                   VALUES("' . $_GET['employeeID'] . '","' . $_GET['firstName'] . '","' . $_GET['lastName'] . '","' . 
                          $_GET['deptName'].'","' . $email.'","' . $phone.'","'.$_GET['cubeNumber'] . '")';
    if(!sqlite_exec($db, $insert_sql, $sqliteError))
    {
        sqlite_close($db);
        die($sqliteError . ", please try again");
    }
    echo '<p>Employee added. <br/> Please refresh your browser before continuing.</p>';
}
// If we are adding a peripheral and have the information we need
elseif('Peripheral' == $_GET['objectType'] && isset($_GET['type']) && isset($_GET['floor']))
{
    $insert_sql = 'INSERT INTO Utilities (Type, Floor, IconID, Longitude, Latitude) 
                   VALUES("'. $_GET['type'] . '",' . $_GET['floor'] .',"' . $_GET['iconName'] .'","' . $_GET['long'] .'","' . $_GET['lat'] .'")';
    if(!sqlite_exec($db, $insert_sql,$sqliteError))
    {
        sqlite_close($db);
        die($sqliteError . ", please try again");
    }
    echo '<p>Object added: Went through clean.</p>';
}
else
{
    echo '<p>You left a field blank.</p>';
}
sqlite_close($db);
?>