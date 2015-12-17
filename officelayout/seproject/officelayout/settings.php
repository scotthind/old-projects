//Authors: Joseph Smith and Christopher Bowen

<?php
session_start(); //Start the session
header( 'Content-Type: text/html; charset=utf-8' );
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Mission Solutions Engineering - Office Layout Tool</title>
 <meta http-equiv='imagetoolbar' content='no'/>
<meta name="description" content="Blank" />
<meta name="keywords" content="Blank" />
<meta name="robots" content="index, follow" />
<link rel="stylesheet" href="css/tipsy.css" media="screen" type="text/css" />
<link rel="stylesheet" href="css/style.css" media="screen" type="text/css" />
<script language="javascript" src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
<script language="javascript" src="js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script language="javascript" src="js/modernizr.js" type="text/javascript"></script>
<script language="javascript" src="js/OpenLayers.js" type="text/javascript"></script>
<script language="javascript" src="js/openlayersadmin.js" type="text/javascript"></script>
<script language="javascript" src="js/tipsy.js" type="text/javascript"></script>
<script language="javascript" src="js/search.js" type="text/javascript"></script>


</head>
<body>
<div id="wrapper">
	<div id="content">
  	<div id="logo"><a href="settings.php"><img src="img/missionSolutionsEngineering_Logo.png" /></a></div>
    <div class="clear"></div>
    <div id="page_header">Settings</div>
    <br />

    <div id="form">
      Below you can change various settings.
      <br /><br />
      <?php
	if(isset($_REQUEST['msg']))
        {
            echo "<span style=\"color:red; font-weight:bold;\">".$_REQUEST['msg']."</span><br />";
        }
      ?>
    	<form action="submit_settings.php" method="post">
        <table width="550px">
    
        <tr>
         <td valign="top">
          <label for="new_pass">Change Password</label>
         </td>
         <td valign="top">
          <input type="password" name="new_pass" maxlength="50" size="30" />
         </td>
        </tr>
         
        <tr>
         <td valign="top">
          <label for="confirm_pass">Confirm New Password</label>
         </td>
         <td valign="top">
          <input type="password" name="confirm_pass" maxlength="50" size="30" />
         </td>
        </tr>
    
        <tr>
         <td valign="top">
          <label for="admin_email">Change Email</label><br />
         </td>
         <td valign="top">
          <input type="text" name="email" maxlength="30" size="30" />
         </td>
        </tr>
            
        <tr>
         <td colspan="2" style="text-align:center">
          <input type="submit" value="Submit" />
         </td>
        </tr>
        </table>
        </form>
        <?php
            // Query the database for the current user's priveleges
            $db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
            $query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
            $result = sqlite_query($db, $query);
            $row = @sqlite_fetch_array($result, SQLITE_ASSOC);
            sqlite_close($db);
            
            // If the user if a humanr, then link back to hr.php
            if('humanr' == $row['userType'])
            {
                echo '<div><a href="hr.php" id="back_home" title="Click here to go back to the human resources page">Back to HR Page</a></div>';
            }
            // Otherwise, they must be an admin -- so link back to admin.php
            else
            {
                echo '<div><a href="admin.php" id="back_home" title="Click here to go back to the admin page">Back to Admin Page</a></div>';
            }
          ?>
    </div>
  </div>
</div>
</body>
</html>