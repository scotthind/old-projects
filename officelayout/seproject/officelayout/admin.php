<?php
session_start(); //Start the session

// Query the database for the current user's priveleges
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
if(!$db)
{
    die($sqliteerror);
}

$query = "SELECT userType FROM user WHERE username=\"".$_SESSION['name']."\"";
$result = sqlite_query($db, $query);
if(!$result)
{
    sqlite_close($db);
    die("Invalid query: " . sqlite_error());
}

$row = @sqlite_fetch_array($result, SQLITE_ASSOC);
if($row['userType'] != 'admin')
{   //If session not registered
    header("location:login.php?msg=You must be an admin to access this page."); // Redirect to login.php page
}
else
{   //Continue to current page
    header( 'Content-Type: text/html; charset=utf-8' );
}
sqlite_close($db);
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
<link rel="stylesheet" href="css/style2.css" media="screen" type="text/css" />
<style type="text/css"> v\:* {behavior:url(#default#VML);}
  #map { height: 600px; width: 1000px; border: 1px solid #888; }
</style>
<script language="javascript" src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
<script language="javascript" src="js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
<script language="javascript" src="js/modernizr.js" type="text/javascript"></script>
<script language="javascript" src="js/OpenLayers.js" type="text/javascript"></script>
<script language="javascript" src="js/openlayersadmin.js" type="text/javascript"></script>
<script language="javascript" src="js/tipsy.js" type="text/javascript"></script>
<script language="javascript" src="js/search.js" type="text/javascript"></script>

</head>
<body onload="init()">
<div id="wrapper">
	<div id="content">
  	<div id="logo"><a href="index.php"><img src="img/missionSolutionsEngineering_Logo.png" /></a></div>
    <div class="clear"></div>
    <div id="page_header">Office Layout Tool and Personnel Locator</div>
    <br />
    <div id="left_section">
      <div id="map_section">
         <div id="map"></div>
      </div>
      <br />
			<div id="levelSelection">
                            <a href="#" onclick="switchLevel(1)" id="level1" style="font-weight:bold; color:#DDD;" title="Click here to see the 1st Floor">Level 1 </a> | <a href="#" onclick="switchLevel(2)" id="level2" title="Click here to see the 2nd Floor">Level 2 </a>
                        </div>

      <div id="map_options">
      	<div class="section_title">Map Options:</div>
		<div>
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="selectFeature"/>Select Feature
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="addFeature"/>Add an object
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="EditObject"/>Edit an object
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="move"/>Move an object
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="addDepartment"/>Add New Department
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="removeDepartment"/>Remove Department
		<input type="radio" name="adminoptions" onclick="toggleControl(this)" value="removeFeature"/>Remove Feature
		</div>
		<hr />
		<div class="section_title">Display Options: </div>
		<input type="checkbox" id="utilities" onclick="showHide()" value="utilities"/>Utilities
		<div id="checkboxes">
		</div>
      </div>
    </div>
    <div id="right_section">
    	<div id="search_box">
      	<strong style="text-transform:uppercase;">Search</strong>&nbsp;&nbsp;&nbsp;<input id="search" type="text" title="Enter a search term. To search for multiple things, place a &quot;,&quot; in between search terms." />
      	<a href="#" onclick="advSearch()"><img src="img/arrow_down.gif" id="search_arrow" title="Click for Advanced Search" /></a>
        <a href="settings.php" id="settings" title="Click here to change administrative settings." class="right"><img src="img/settings_icon.png" border="0" /></a>
        <br class="clear" />
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
      <div style="float:right;">
	<div id="search_results">Enter the term of the subject you are looking for in the search box above.<br /><br />To search for multiple things, place a "," in between search terms.</div>
	</div>
      <div id="result_details">
      	<div class="section_title">Currently Selected:</div>
			<div id="selected_feature">
        
		 </div>
	  </div>
      <br />
      <br />
	<div id="editField" style="display:none; height:600px; width:150px;">
		<div id="personEdit" style="display:none;">
		<p>
			First Name: <br/><input type="text" id="firstNameTextEdit"/>
			Last Name: <br/><input type="text" id="lastNameTextEdit"/>
			Phone: <br/><input type="text" id="phoneEdit"/>
			Email: <br/><input type="text" id="emailEdit"/>
			Cubicle Number: <br/><input type="text" id="cubicleEdit"/>
			Current Department: <br/><input type="text" id="currentDepartment" disabled="disabled"/>
			Department: <br/><select id="editPersonDepartments"></select>
                </p>
			<input type="text" id="editHiddenPersonID" style="display:none;"/>
			<button value="editPerson" onclick="edit(false, this)">Save Edit</button>
			<p id="areYouSurePerson" style="display:none;">Are You Sure ?</p>
			<button id="sureButtonEditPerson" style="display:none;" value="editPerson" onclick="edit(true, this)">Yes</button>
		</div>
		<div id="utilityEdit"  style="display:none;">
			Type: <br/><input type="text" id="utilityTypeEdit"/>
			<input type="text" id="editHiddenUtilityID" style="display:none;"/>
			Current Icon: <br/><input type="text" id="currentIcon" disabled="disabled"/>
			Icon: <br/><select id="editUtilityIcons"></select>
			<button value="editUtility" onclick="edit(false, this)">Save Edit</button>
			<p id="areYouSureUtility" style="display:none;">Are You Sure ?</p>
			<button id="sureButtonEditUtility" style="display:none;" value="editUtility" onclick="edit(true, this)">Yes</button>
		</div>
		<br/>
		
		
		
		<p id="editFeature_response"></p>
	</div>
	<div id="removeField" style="display:none; height:600px; width:150px;">
		<div id="removePerson" style="display:none;">
		<p>
			First Name: <br/><input type="text" id="firstNameTextRemove" disabled="disabled"/>
			Last Name: <br/><input type="text" id="lastNameTextRemove" disabled="disabled"/>
			Phone: <br/><input type="text" id="phoneRemove" disabled="disabled"/>
			Email: <br/><input type="text" id="emailRemove" disabled="disabled"/>
			Cubicle Number: <br/><input type="text" id="cubicleRemove" disabled="disabled"/>
			Current Department: <br/><input type="text" id="currentDepartmentRemove" disabled="disabled"/>
                </p>	
			<input type="text" id="removeHiddenPersonID" style="display:none;"/>
			<button value="removePerson" onclick="remove(false, this)">Remove Feature</button>
			<p id="areYouSurePersonRemove" style="display:none;">Are You Sure ?</p>
			<button id="sureButtonRemovePerson" style="display:none;" value="removePerson" onclick="remove(true, this)">Yes</button>
		</div>
		<div id="utilityRemove"  style="display:none;">
			Type: <br/><input type="text" id="utilityTypeRemove"disabled="disabled"/>
			<input type="text" id="removeHiddenUtilityID" style="display:none;"/>
			Current Icon: <br/><input type="text" id="currentIconRemove" disabled="disabled"/>
			
			<button value="removeUtility" onclick="remove(false, this)">Remove Feature</button>
			<p id="areYouSureUtilityRemove" style="display:none;">Are You Sure ?</p>
			<button id="sureButtonRemoveUtility" style="display:none;" value="removeUtility" onclick="remove(true, this)">Yes</button>
		</div>
		<br/>
		
		
		
		<p id="removeFeature_response"></p>
	</div>
	<div id="addDepartment" style="display:none; height:600px; width:150px;">
		New Department's name: <input type="text" onkeypress="return alpha(event, alphanumeral)" id="newDeptName"/>
		<br/>
		<hr/>
		Select the Icon Color: <select id="iconDropdown"> </select>
		<br/><hr/>
		<button value="Add New Department" onclick="addNewDepartment()">Add New Department</button>
		
		<p id="addDepartment_response"></p>
	</div>
	<div id="removeDepartment" style="display:none; height:600px; width:150px;">
		<select id="removeDepartmentSelect"></select>
		
		
		<button value="Remove Department" onclick="removeDepartment(false)">Remove Department</button>
		
		<p id="removeDepartment_response"></p>
		<button id="sureButton" style="display:none;" value="yes" onclick="removeDepartment(true)">Yes</button>
	</div>
	<div id="insertNew" style="display:none; height:600px; width:150px;">
		<select id="addFeatureSelect" onChange="showInsert(this)">Select Type
		<option>Employee</option>
		<option>Utility</option></select>
		<div id="employeeInsert">
		First Name: <input type="text" onkeypress="return alpha(event, letters)" id="newFirstName"/>
		Last Name: <input type="text" onkeypress="return alpha(event, letters)" id="newLastName"/>
		Phone Number(ext): <input type="text" onkeypress="return alpha(event, numbers)" id="newPhone"/>
		Email: <input type="text" onkeypress="return alpha(event, email)" id="newEmail"/>
		Cube Number: <input type="text"  onkeypress="return alpha(event, alphanumeral)"  id="newCubeNumber"/>
		Department: <select id="departmentSelect"></select>
		</div>
		<div id="utilityInsert" style="display:none;">
		Type: <input type="text" id="newType"/>
		Icon: <select id="iconSelect">
		</select>
		</div>
		Floor: <input type="text" id="newFloor" disabled="disabled"/>
			Longitude: <input type="text" id="newLong" disabled="disabled"/>
			Latitude: <input type="text" id="newLat" disabled="disabled"/>
			<br/><br/>
			<button value="Insert New Object" onclick="insert()">Insert New Object</button>
			<br/><br/>
			<p id="insert_response"></p>
		
	</div>
	<div id="saveMove" style="display:none; height:600px; width:150px;">
			Longitude: <input type="text" id="moveLong" disabled="disabled"/>
			Latitude: <input type="text" id="moveLat" disabled="disabled"/>
			<input type ="text" id="hiddenUtilID" style="display:none;"/>
			<input type="text" id="hiddenPersonID" style="display:none;"/>
			<input type="text" id="hiddenType" style="display:none;"/>
			<br/><br/>
			<button value="save" onclick="update()">Save New Location</button>
			<p id="update_response"></p>
		
	</div>
	<div id="removeFeature" style="display:none; height:600px; width:150px;">
		<div id="removeFeatureInfo">
			
		</div>
			Longitude: <input type="text" id="moveLong" disabled="disabled"/>
			Latitude: <input type="text" id="moveLat" disabled="disabled"/>
			<input type ="text" id="hiddenUtilID" style="display:none;"/>
			<input type="text" id="hiddenPersonID" style="display:none;"/>
			<input type="text" id="hiddenType" style="display:none;"/>
			<br/><br/>
			<button value="remove" onclick="removeFeature(false)">Remove Feature</button>
			
			<p id="update_response"></p>
			<button id="sureRemoveButton" style="display:none;" value="yes" onclick="removeFeature(true)">Yes</button>
	</div>
      <div id="admin_login">
      	<a href="logout.php" id="login_text" title="Click here to logout">Log Out</a>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</div>
</body>
</html>


