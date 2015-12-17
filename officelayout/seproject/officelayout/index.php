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
<style type="text/css"> v\:* {behavior:url(#default#VML);}
  #map { height: 600px; width: 1000px; border: 1px solid #888; }
</style>
<script language="javascript" src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
<script language="javascript" src="js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script language="javascript" src="js/modernizr.js" type="text/javascript"></script>
<script language="javascript" src="js/OpenLayers.js" type="text/javascript"></script>
<script language="javascript" src="js/openlayersproject.js" type="text/javascript"></script>
<script language="javascript" src="js/tipsy.js" type="text/javascript"></script>
<script language="javascript" src="js/search.js" type="text/javascript"></script>
</head>
<body onload="init()">
<div id="wrapper">
	<div id="content">
  	<div id="logo"><a href="index.php"><img src="img/missionSolutionsEngineering_Logo.png" /></a></div>
    <div class="clear"></div>
    <div id="page_header">Office Layout Tool and Personnel Locator </div>
    <br />
    <div id="left_section">
      <div id="map_section">
         <div id="map"></div>
      </div>
      <br />
	<div id="levelSelection"><a href="#" onclick="switchLevel(1)" id="level1" style="font-weight:bold; color:#DDD;" title="Click here to see the 1st Floor">Level 1 </a> | <a href="#" onclick="switchLevel(2)" id="level2" title="Click here to see the 2nd Floor">Level 2 </a></div>
      <div id="map_options">
      	<div class="section_title">Map Options:</div>
		<input type="checkbox" id="utilities" onclick="showHide()" value="utilities"/>Utilities
		<div id="checkboxes">
		</div>
		
      </div>
    </div>
    <div id="right_section">
    	<div id="search_box">
      	<strong style="text-transform:uppercase;">Search</strong>&nbsp;&nbsp;&nbsp;<input id="search" type="text" title="Enter a search term. To search for multiple things, place a &quot;,&quot; in between search terms." />
      	<a href="#" onclick="advSearch()"><img src="img/arrow_down.gif" id="search_arrow" title="Click for Advanced Search" /></a>
        <div id="adv_options">
          <br />
        	Filter Search: <select id="search_filter">
          	<option value="name">By Person</option>
            <option value="department">By Department</option>
            <option value="email">By Email</option>
            <option value="phone">By Phone</option>
        	</select>
        </div>
      </div>
      <br />
      <div id="search_results">Enter the term of the subject you are looking for in the search box above.<br /><br />To search for multiple things, place a "," in between search terms.</div>
      <br />
      <div id="result_details">
      	<div class="section_title">Currently Selected:</div>
			<div id="selected_feature">[Nothing selected]</div>
	  </div>
      <br />
      <br />
      <div id="admin_login">
      	<a href="login.php?msg=" id="login_text" title="Click here to login">Admin/HR Login</a>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</div>
</body>
</html>
