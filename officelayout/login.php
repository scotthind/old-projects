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
<!--[if IE 7]><link rel="stylesheet" href="css/IE7admin.css" type="text/css" /><![endif]-->
<script src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
<script src="js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script src="js/modernizr.js" type="text/javascript"></script>
<script src="js/tipsy.js" type="text/javascript"></script>
<script src="js/search.js" type="text/javascript"></script>
</head>
    
<body>
    
<div id="maincontainer">
	<!-- MSE logo -->
  	<div id="loginlogo">
		<a href="index.html"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo"/></a>
	</div>
    <div class="clear"></div>
    <br />
    <div id="login_section">
		
        <?php //If there was an error message, echo it out
        if(isset($_GET['msg']))
        {
            echo '<p>' . $_GET['msg'] . '</p>';
        }
       ?> 
    <!-- The body of the login page -->   
    <h3>Please enter your Login Information</h3>
     <form name="login" id="login" method="post" action="check_login.php">
      <table id="logintable">
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


<!-- Link back to the main page -->
<a href="index.html" id="gohome" title="Click here to go back to the main page.">Back to main page</a>
    </div>
    <div class="clear"></div>
</div>
</body>
</html>
