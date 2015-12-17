<?php //Authors: Joseph Smith and Christopher Bowen
session_start(); //Start the current session
session_destroy(); //Destroy it! So we are logged out now
header("location:index.html?msg=You have successfully logged out."); // Move back to index.html with a logout message
?>
