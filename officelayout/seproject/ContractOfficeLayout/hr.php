<?php
//Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the session
// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
$query = "SELECT userType FROM user WHERE username=\"" . $_SESSION['name'] . "\"";
$result = sqlite_query($db, $query);
if (false == $result) {
    sqlite_close($db);
    die("Database error: " . $sqlite_error);
}
$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
sqlite_close($db);
if ($row['userType'] != 'humanr') {   //If session not registered
    header("location:login.php"); // Redirect to login.php page
} else {   //Continue to current page
    header('Content-Type: text/html; charset=utf-8');
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Human Resources - New Personnel Submission</title>
        <link rel="stylesheet" href="css/style.css" media="screen" type="text/css" />
        <link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
    </head>
    <body>
        <div id="maincontainer">
            <div id="logo">
                <a href="index.php"><img src="../../MSEOfficeLayout/img/missionSolutionsEngineering_Logo.png" /></a></div>
            <div id="topsection"><div class="innertube">
                    <br /><div id="page_header">Human Resources</div>
                    <div id="admin_login">
                        <a href="logout.php" id="login_text" title="Click here to logout">Log Out</a>
                    </div></div>
            </div>
            <div id="form">
                <p class="formtext"><strong> Below you can submit a request to the IT Department to add a new employee.
                        <br />
                        Fields marked with a * are required.
                        <br />
                        At least one of the fields marked with ** is required.
                </p></strong>
                <br /><br />
                <form action="sendMail.php" method="post" name="infoChangeForm" id="infoChangeForm">
                    <table width="450px">
                        <tr>
                            <td valign="top">
                                <label for="employee_id">Employee ID *</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="employee_id" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="first_name">First Name *</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="first_name" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="last_name">Last Name *</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="last_name" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="email">Email Address **</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="email" maxlength="80" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="telephone">Telephone Number **</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="telephone" maxlength="30" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="cubicleNum">Cubicle Number *</label>
                            </td>
                            <td valign="top">
                                <input  type="text" name="cubicleNum" maxlength="30" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <label for="comments">Comments</label>
                            </td>
                            <td valign="top">
                                <textarea  name="comments" maxlength="1000" cols="25" rows="6"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="text-align:center">
                                <input type="submit" value="Submit" />   <input type="reset" value="Reset" />
                            </td>
                        </tr>
                    </table>
                </form>
                <div id="gohome">
                    <a href="index.php" id="back_home" 
                       title="Click here to go back to the main page">Back to main page</a>
                    <div id="hrsettings">
                        <a href="settings.php" id="settings" title="Click here to change settings."
                           class="right"><img src="../../MSEOfficeLayout/img/settings_icon.png" border="0" /></a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>