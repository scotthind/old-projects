//Authors: Joseph Smith and Christopher Bowen

<?php
session_start(); //Start the current session
session_destroy(); //Destroy it! So we are logged out now
header("location:index.php?msg=You have successfully logged out."); // Move back to index.php with a logout message
?>