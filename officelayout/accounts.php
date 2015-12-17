<?php
//Authors: Joseph Smith and Christopher Bowen
require_once 'connectDB.php';
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = connect('SQLiteDB/OfficeLayout.db', 'admin');
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
	<title>Account Management</title>
	<link rel="shortcut icon" href="img/mse.jpg" />
	<link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
	<link rel="stylesheet" href="css/userStyle.css" media="screen" type="text/css" />
	<!--[if IE]><link rel="stylesheet" href="css/IEstyle.css" media="screen" type="text/css" /><![endif]-->
	<!--[if IE 7]><link rel="stylesheet" href="css/IE7style.css" type="text/css" /><![endif]-->
	<script type="text/javascript">
		/**
		 * This function disables all input fields that are extraneous to removing a user.
		 * @param isDisabled True when the user is removing an account and false otherwise.
		 */
		function removeUser(isDisabled) {
			document.getElementById('employeeID').disabled = isDisabled;
			document.getElementById('password').disabled = isDisabled;
			document.getElementById('confirm_password').disabled = isDisabled;
			document.getElementById('usertype').disabled = isDisabled;
		}
	</script>
</head>
<body>
<div id="maincontainer">
	<!-- MSE Logo -->
	<div id="logo">
		<a href="admin.php"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo"/></a></div>
	<!-- Title bar of the page -->
	<div id="topsection"><div class="innertube">
		<br /><div id="page_header">Account Management</div>
		<br /></div>
    </div>
	<!-- Start of the form to add or remove accounts -->
	<div id="form">
		<!-- Description of the form visible to the user -->
		<p class="formtext"><strong> Below you can add or remove administrator or human resources accounts.
			<br />
				All fields are required.
		</strong></p>
		<br /><br />
		<script type="text/javascript">
		    /*This function determines which php script to send the submitted information to.*/
			function OnSubmitForm()
			{
				if(document.accountForm.addremove[0].checked == true)
				{
					document.accountForm.action = "addAccount.php";
				}
				else if(document.accountForm.addremove[1].checked == true)
				{
					document.accountForm.action = "removeAccount.php";
				}
				return true;
			}
		</script>
		<form name="accountForm" onsubmit="return OnSubmitForm();" method="post">
			<table id="accountstable">
				<tr>
					<td class="accounts_element">
						<!-- Add or remove radio buttons for what is being done to an account -->
						<label>Add or Remove</label>
					</td>
					<td class="accounts_element">
						<input name="addremove" type="radio" value="add"  checked onclick="removeUser(false)" />Add
						<br />
						<input name="addremove" type="radio" value="remove" onclick="removeUser(true)" />Remove
					</td>
				</tr>
				<tr>
					<td class="accounts_element">
						<!-- The only field used in BOTH add and remove -->
						<label for="username">Username</label>
					</td>
					<td class="accounts_element">
						<input  type="text" name="username" id="username" maxlength="50" size="30" />
					</td>
				</tr>
				<tr>
					<td class="accounts_element">
						<label for="employeeID">Employee ID</label>
					</td>
					<td class="accounts_element">
						<input  type="text" name="employeeID" id="employeeID" maxlength="50" size="30" />
					</td>
				</tr>
				<tr>
					<td class="accounts_element">
						<label for="password">Password</label>
					</td>
					<td class="accounts_element">
						<input  type="password" name="password"  id="password" maxlength="80" size="30" />
					</td>
				</tr>
				<tr>
					<td class="accounts_element">
						<label for="confirm_password">Confirm Password</label>
					</td>
					<td class="accounts_element">
						<input  type="password" name="confirm_password" id="confirm_password" maxlength="30" size="30" />
					</td>
				</tr>
				<tr>
                    <td class="accounts_element">
						<!-- Drop down menu for the type of account that is being added -->
                        <label for="usertype">Account Type</label>
                    </td>
                    <td class="accounts_element">
                        <select name="usertype" id="usertype">
						<option value="humanr">Human Resources</option>
						<option value="admin">Administrator</option></select>
                    </td>
				</tr>
				<tr>
					<td class="account_buttons" colspan="2">
						<input type="submit" value="Submit" />   
						<input type="reset" value="Reset" onclick="removeUser(false)" />
					</td>
				</tr>
			</table>
		</form>
		<div id="gohome">
			<!-- Link back to admin page -->
			<a href="admin.php"
				title="Click here to go back to the main page">Back to main page</a>
		</div>
	</div>
</div>
</body>
</html>
