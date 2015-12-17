//Authors: Joseph Smith and Christopher Bowen

<?php
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
sqlite_close($db);

if($row['userType'] != 'humanr')
{   //If session not registered
    header("location:login.php"); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Human Resources - New Personnel Submission</title></head>
<head>
<link rel="stylesheet" href="css/style.css" media="screen" type="text/css" />
</head>
<body>
<div id="wrapper">
 <div id="content">
  	<div id="logo"><a href="index.php"><img src="img/missionSolutionsEngineering_Logo.png" /></a></div>
    <div class="clear"></div>
    <div id="page_header">Human Resources</div>
    <br />

  <div id="form">
   <p> Below you can submit a request to the IT Department to add a new employee.
     <br />
       Fields marked with * are required.
     <br />
       At least one of the fields marked with ** is required.
   </p>
   <br /><br />
   <form action="sendMail.php" method="post" name="infoChangeForm" id="infoChangeForm">
       <table width="450px">

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
	  <label for="officeNum">Office Number</label>
	 </td>
	 <td valign="top">
	  <input  type="text" name="officeNum" maxlength="30" size="30" />
	 </td>
	</tr>

	<tr>
	 <td valign="top">
	  <label for="comments">Comments </label>
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
       <div><a href="index.php" id="back_home" title="Click here to go back to the main page">Back to main page</a></div>
  </form>
 </div>
</div>
</div>
</body>
</html>