<?php
//Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
session_start(); //Start the session

// Query the database for the current user's priveleges

$db = connect('SQLiteDB/OfficeLayout.db', 'humanr');
if(null == $db || !$db)
{
    die();
}
$db->close();
?>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="Author" content="Joseph Smith and Christopher Bowen" />
        <title>Human Resources - New Personnel Submission</title>
		<link rel="shortcut icon" href="img/mse.jpg" />
        <link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
        <link rel="stylesheet" href="css/userStyle.css" media="screen" type="text/css" />
		<!--[if IE]><link rel="stylesheet" href="css/IEstyle.css" media="screen" type="text/css" /><![endif]-->
		<!--[if IE 7]><link rel="stylesheet" href="css/IE7style.css" type="text/css" /><![endif]-->
    </head>
    <body>
        <div id="maincontainer">
			<!-- MSE Logo -->
            <div id="logo">
                <a href="index.html"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a></div>
            <!-- Page title bar -->
			<div id="topsection"><div class="innertube">
                    <br /><div id="page_header">Human Resources</div>
					<!-- Settings link for Human Resource personnel -->
					<div id="hrsettings">
						<a href="settings.php" id="settings" title="Click here to change settings."
							class="right"><img src="img/settings_icon.png" alt="Settings" /></a>
					</div>
					<!-- Logout Link -->
                    <div id="admin_login">
                        <a href="logout.php" id="login_text" title="Click here to logout">Log Out</a>
                    </div></div>
            </div>
            <div id="form">
				<!-- Form description for the user -->
                <p class="formtext"><strong>Below you can submit a request to change or add employee information.
                        <br />
                        Fields marked with * are required.
                        <br />
                        At least one of the fields marked with ** is required.
                </strong></p>
                <br /><br />
                <form action="sendMail.php" method="post" name="infoChangeForm" id="infoChangeForm">
					<!-- Fields that the HR personnel can/must fill in for requests to be made -->
                    <table id="hr_table">
                        <tr>
                            <td class="hr_element">
                                <label>Employee ID *</label>
                            </td>
                            <td class="hr_element">
                                <input  type="text" name="employee_id" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>First Name *</label>
                            </td>
                            <td class="hr_element">
                                <input  type="text" name="first_name" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>Last Name *</label>
                            </td>
                            <td class="hr_element">
                                <input type="text" name="last_name" maxlength="50" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>Email Address **</label>
                            </td>
                            <td class="hr_element">
                                <input  type="text" name="email" maxlength="80" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>Telephone Number **</label>
                            </td>
                            <td class="hr_element">
                                <input  type="text" name="telephone" maxlength="30" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>Cubicle Number *</label>
                            </td>
                            <td class="hr_element">
                                <input  type="text" name="cubicleNum" maxlength="30" size="30" />
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_element">
                                <label>Comments</label>
                            </td>
                            <td class="hr_element">
                                <textarea  name="comments" maxlength="1000" cols="25" rows="6"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td class="hr_buttons" colspan="2">
                                <input type="submit" value="Submit" />   
								<input type="reset" value="Reset" />
                            </td>
                        </tr>
                    </table>
                </form>
                <div id="gohome">
					<!-- Link back to the main page -->
                    <a href="index.html"
                       title="Click here to go back to the main page">Back to main page</a>
                </div>
            </div>
		</div>
    </body>
</html>
