//Authors: Joseph Smith and Christopher Bowen

<?php
ini_set('display_errors', false);
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
$row = @sqlite_fetch_array($result, SQLITE_ASSOC);

if($row['userType'] != 'admin')
{   //If session not registered
    header("location:login.php?msg=You must be an admin to access this page."); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}

// Begin the process of inserting the object

if(isset($_GET['objectType']))
{
    if($_GET['objectType'] == "Employee" && isset($_GET['firstName'])&& isset($_GET['lastName'])&& isset($_GET['floor']))
    {
        $firstName = $_GET['firstName'];
	$lastName = $_GET['lastName'];
	$deptName = $_GET['deptName'];
	$lat = $_GET['lat'];
	$long = $_GET['long'];
        
	//Start checking the optional fields
	if(isset($_GET['phone']))
	{
            $phone = $_GET['phone'];
	}
	else
        {
            $phone = "N/A";
	}
        
	if(isset($_GET['email']))
	{
            $email = $_GET['email'];
	}	
	else
        {
            $email = "N/A";
	}
        
	if(isset($_GET['floor']))
	{
            $floor = $_GET['floor'];
	}	
	else
        {
            $email = "N/A";
	}
		
	if(isset($_GET['cubeNumber']))
	{
            $cubeNumber = $_GET['cubeNumber'];
	}	
	else
        {
            $cubeNumber = "N/A";
	}
        
	//We need to be careful when inserting a new object because we need to add it to the cubicle and personnel table
	$insert_sql = 'INSERT INTO Personnel (firstName, lastName, phone, email, deptName) VALUES("'. $firstName. '","' . $lastName.'","' . $phone.'","' . $email.'","'.$deptName. '")';
	$insert= sqlite_exec($db, $insert_sql,$sqliteError);
	if(!$insert)
        {
            die($sqliteError . ", please try again");
        }
			 
	//or die(sqlite_error_string($sqlite_error));
	//this gets the last added person to the personnel table
	$selectlastinsert = 'Select max(personID) from Personnel';
	$result = sqlite_query( $db, $selectlastinsert);
	$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
	$lastID = $row['max(personID)'];
        
	//Now we also insert into cubicle
	$insert_sql2 = 'INSERT INTO cubicle (cubeNumber, loclat, loclong, personID, floor) VALUES("' . $cubeNumber . '",' . $lat . ',' . $long . ',"' . $lastID . '",'.$floor.')';
	$insert2= sqlite_query($db, $insert_sql2); 
	echo 'Object added: Went through clean <br/>Please refresh your browser, before continuing';	
    }
    
    //utility is simple, we just insert into utility if all the fields are set
    elseif($_GET['objectType'] == "Utility" && isset($_GET['type']) && isset($_GET['floor']))
    {
        $type = $_GET['type'];
        $iconName = $_GET['iconName'];
        $long = $_GET['long'];
        $lat = $_GET['lat'];
        $floor = $_GET['floor'];
        $insert_sql = 'INSERT INTO Utilities (type, floor, iconID, long, lat) VALUES("'. $type. '",'.$floor.',"' . $iconName.'","' . $long.'","' . $lat.'")';
        $insert= sqlite_exec($db, $insert_sql,$sqliteError);
        if(!$insert)
        {
            die($sqliteError . ", please try again");
        }
        echo 'Object added: Went through clean';
    }
    else
    {
        echo 'You left a field blank';
    }
    sqlite_close($db);
}
else
{
    echo 'Error! Please fill all fileds!';
    sqlite_close($db);
}
?>