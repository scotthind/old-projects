//Authors: Joseph Smith and Christopher Bowen

<?php
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

// Begin the process of removing a department

if(isset($_GET['departmentName'])  && isset($_GET['sure']))
{
    $deptName = $_GET['departmentName'];
    $sure = $_GET['sure'];
    if($deptName == "No Department")
    {
        echo "Sorry I cannot remove that department, please remove the individuals in that department to remove them<br/> Ok?";
    }	
    else
    {
        if($sure == "false")
	{
            $query = "SELECT count(*) FROM Personnel WHERE deptName = \"". $deptName."\"" ;
            $result = sqlite_query($db, $query);
            if(!$result)
            {
                sqlite_close($db);
		die("Invalid query: " . sqlite_error());
            }
	    // Iterate through the rows, adding XML nodes for each
	    while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
            {
		echo $row['count(*)'] . ' rows will be affected, continue?';		
            }
	}
	elseif($sure == "true")
	{
            $delete_sql = "DELETE from Department where deptName = \"".$deptName."\"";
            $result = sqlite_query($db, $delete_sql, SQLITE_ASSOC, $sqlite_error);
            if (false == $result)
            {
                sqlite_close($db);
		die("SQLite error: nope this one " . $sqlite_error);	
            }
            $update_sql = "UPDATE Personnel set deptName = \"No Department\" where deptName = \"".$deptName."\"";
            $result = sqlite_query($db, $update_sql, SQLITE_ASSOC, $sqlite_error);
            if(false == $result)
            {
                sqlite_close($db);
		die("SQLite error: this one" . $sqlite_error);
            }
            echo $deptName." has been removed<br/>Please refresh your browser";
	}
    }
}
sqlite_close($db);
?>