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
<script language="javascript" src="js/tipsy.js" type="text/javascript"></script>
<script language="javascript" src="js/search.js" type="text/javascript"></script>
</head>
    
<body>
    
<div id="wrapper">
  <div id="content">
  	<div id="logo"><a href="index.php"><img src="img/missionSolutionsEngineering_Logo.png" /></a></div>
    <div class="clear"></div>
    <br />
    <div id="login_section">
        <?php
        if (isset($_GET['msg']))
        {
            echo '<p>' . $_GET['msg'] . '</p>';
        }
        
        echo <<<EOD
    <h3>Please enter your Login Information</h3>
    <form name="login" id="login" method="POST" action="check_login.php">
     <table border="0" cellspacing="2" cellpadding="2" align="center">
      <tr>
       <td>
        <label for="username">Username: </label>
       </td>
       <td>
        <input type="text" name="username" id="username" placeholder="Enter Username" style="padding:4px; border:1px #CCC solid; width:120px;" />
       </td>
      </tr>
      <tr>
       <td>
        <label for="password">Password: </label>
       </td>
       <td>
        <input type="password" size="40" name="password" id="password" placeholder="Enter Password" style="padding:4px; border:1px #CCC solid; width:120px;" />
       </td>
      </tr>
     </table>
     <p><input type="submit" name="submit" id="submit" value="Submit"/> <input type="reset" name="reset" id="reset" value="Reset"/></p>
    </form>
EOD;
        ?>
<a href="index.php" id="back_home" title="Click here to go back to the main page.">Back to main page</a>
    </div>
    <div class="clear"></div>
  </div>
</div>
</body>
</html>