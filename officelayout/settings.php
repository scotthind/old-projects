<?php //Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the session
if(!isset($_SESSION['name']))
{
    header("location:login.php?msg=You must log in to access this page.");
}
else{
    header( 'Content-Type: text/html; charset=utf-8' );
}

// Query the database for the current user's priveleges
// Store the user type for later use 
$db = new SQLITE3('SQLiteDB/OfficeLayout.db', SQLITE3_OPEN_READONLY);
$query = 'SELECT userType FROM User Where Username=:username';
$statement = $db->prepare($query);
$statement->bindParam(':username', $_SESSION['name'], SQLITE3_TEXT);
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
$db->close();;
?>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Mission Solutions Engineering - Office Layout Tool</title>
    <meta name="description" content="Blank" />
    <meta name="keywords" content="Blank" />
    <meta name="robots" content="index, follow" />
	<meta name="Author" content="Joseph Smith and Christopher Bowen" />
	<link rel="shortcut icon" href="img/mse.jpg" />
    <link rel="stylesheet" href="css/tipsy.css" media="screen" type="text/css" />
    <link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
    <link rel="stylesheet" href="css/userStyle.css" media="screen" type="text/css" /> 
	<!--[if IE]><link rel="stylesheet" href="css/IEstyle.css" media="screen" type="text/css" /><![endif]-->
	<!--[if IE 7]><link rel="stylesheet" href="css/IE7style.css" type="text/css" /><![endif]-->
</head>
<body>
    <div id="maincontainer">
            <div id="logo">
                <?php
                    // If the user is a humanr, then link back to hr.php
                    if('humanr' == $row['UserType'])
                    {
                            echo '<a href="hr.php"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a>';
                    }
                    // Otherwise, they must be an admin -- so link back to admin.php
                    else
                    {
                            echo '<a href="admin.php"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a>';
                    }
                ?>
                </div>
                
			<!-- The title bar of the page -->
            <div id="topsection"><div class="innertube">	
                    <br /><div id="page_header">Settings</div><br />
            </div></div>
			<!-- Start of the settings form -->
			<!-- The form gives the options to change the password or email
					of the user currently logged in. -->
            <div id="form">
                    <p class="formtext"><strong>Below you can change your account settings.</strong></p>
                    <br />
                    <?php
							// Used to display success or failure messages to the user
                            if(isset($_REQUEST['msg']))
                            {
                                    echo "<span style=\"color:red; font-weight:bold;\">".$_REQUEST['msg']."</span><br />";
                            }
                    ?>
                    <form action="submit_settings.php" method="post">
                        <table id="settings_table">
                            <tr>
                                <td class="settings_element">
                                    <label>Change Password</label>
                                </td>
                                <td class="settings_element">
                                    <input type="password" name="new_pass" maxlength="50" size="30" />
                                </td>
                            </tr>
                            <tr>
                                <td class="settings_element">
                                    <label>Confirm New Password</label>
                                </td>
                                <td class="settings_element">
                                    <input type="password" name="confirm_pass" maxlength="50" size="30" />
                                </td>
                            </tr>
                            <tr>
                                <td class="settings_element">
                                    <label>Change Email</label><br />
                                </td>
                                <td class="settings_element">
                                    <input type="text" name="email" maxlength="30" size="30" />
                                </td>
                            </tr>
                            <tr>
                                <td class="settings_button" colspan="2">
                                    <input type="submit" value="Submit" />
                                </td>
                            </tr>
                        </table>
                    </form>
                    <?php
                            // If the user is a humanr, then link back to hr.php
                            if('humanr' == $row['UserType'])
                            {
                                    echo '<div id="gohome"><a href="hr.php" id="back_home" title="Click here to go back to the human resources page">Back to HR Page</a></div>';
                            }
                            // Otherwise, they must be an admin -- so link back to admin.php
                            else
                            {
                                    echo '<div id="gohome"><a href="admin.php" id="back_home" title="Click here to go back to the admin page">Back to Admin Page</a></div>';
                            }
                    ?>
            </div>
    </div>
</body>
</html>
