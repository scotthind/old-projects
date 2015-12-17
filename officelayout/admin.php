<?php //Authors: Jospeh Smith and Christopher Bowen
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
	<title>Mission Solutions Engineering - Office Layout Tool</title>
	<meta name="description" content="Blank" />
	<meta name="keywords" content="Blank" />
	<meta name="robots" content="index, follow" />
	<meta name="Author" content="Joseph Smith and Christopher Bowen" />
	<link rel="shortcut icon" href="img/mse.jpg" />
	<link rel="stylesheet" href="css/tipsy.css" media="screen" type="text/css" />
	<link rel="stylesheet" href="css/commonStyle.css" media="screen" type="text/css" />
	<link rel="stylesheet" href="css/adminStyle.css" media="screen" type="text/css" />
	<!--[if IE]><link rel="stylesheet" href="css/IEstyle.css" media="screen" type="text/css" /><![endif]-->
	<!--[if IE 7]><link rel="stylesheet" href="css/IE7admin.css" type="text/css" /><![endif]-->
	<style type="text/css"> v\:* {behavior:url(#default#VML);}
		#map { height: 570px; width: 790px; border: 0px solid #888; }
	</style>
	<script src="js/jquery-1.6.2.min.js" type="text/javascript"></script>
	<script src="js/jquery-ui-1.8.16.custom.min.js" type="text/javascript"></script>
	<script src="js/modernizr.js" type="text/javascript"></script>
	<script src="js/OpenLayers.js" type="text/javascript"></script>
    <script src="js/common.js" type="text/javascript"></script>
	<script src="js/adminUser.js" type="text/javascript"></script>
	<script src="js/tipsy.js" type="text/javascript"></script>
	<script src="js/search.js" type="text/javascript"></script>
</head>
<body onload="adminInit()">
	<div id="maincontainer">
		<!-- MSE Logo -->
		<div id="logo">
			<a href="admin.php"><img src="img/missionSolutionsEngineering_Logo.png" alt="MSE Logo" /></a>
		</div>
		<!-- Page title bar -->
		<div id="topsection"><div class="innertube">
			<br /><div id="page_header">Office Layout Tool and Personnel Locator</div>
			<!-- Admin settings link -->
			<div id="admin_settings">
				<a href="settings.php" id="settings" title="Click here to change settings."
					class="right"><img src="img/settings_icon.png" alt="Settings" /></a>
			</div>
			<!-- Login for admin and HR personnel -->
			<div id="admin_login">
				<a href="accounts.php" id="modify_text" 
					title="Click here to modify user accounts">Manage Accounts</a> &nbsp; &nbsp;
				<a href="logout.php" id="login_text" title="Click here to logout">Log Out</a> 
			</div></div>
		</div>
		<div id="contentwrapper">
			<!-- Main page section containing the map -->
			<div id="contentcolumn"><div class="innertube">
				<div id="map_section">
					<div id="map"></div>
					<div id="otherfloor" style="display:none;"></div>
				</div>
				<br />
			</div></div>
		</div>
		<!-- Right section of the page containing the search and selection features -->
		<div id="rightcolumn"><div class="innertube">
			<div id="search_box">
				<strong style="text-transform:uppercase;">Search</strong>&nbsp;&nbsp;&nbsp;
				<!-- Hover text for the search bar -->
				<input id="search" type="text" 
					title="Enter a search term. To search for multiple things, place a comma 
						in between search terms." />
				<a href="#" onclick="advSearch()"><img src="img/arrow_down.gif" 
					id="search_arrow" title="Click for Advanced Search" alt="" /></a>
				<!-- Advanced options dropdown menu -->
				<div id="adv_options">
					<br />
					Filter Search: <select id="search_filter">
						<option value="name">By Name</option>
						<option value="department">By Department</option>
						<option value="email">By Email</option>
						<option value="phone">By Phone</option>
					</select>
				</div>
			</div>
			<br />
			<!-- Filler text for the search box -->
			<div style="float:right;">
				<div id="search_results">Enter the information of the employee you are looking for in the search box above.
					<br /><br />To search for multiple individuals, place a comma in between search terms.</div>
			</div>
		</div></div>
		<!-- Search results area moved to a new window -->
		<div id="leftcolumn"><div class="innertube">
			<div id="result_details">
				<!-- Map selections show here -->
				<div id="current_selection">
					<strong style="text-transform:uppercase;">Currently Selected</strong></div>
				<div id="selected_feature">[Nothing selected]</div>
			</div>
			<br />
			<br />
			<div id="form_area">
				<div id="editField" style="display:none; height:600px; width:150px;">
					<!-- Edit a current employee's information -->
					<div id="employeeEdit" style="display:none;">
						<p>
							First Name: <br/><input type="text" id="firstNameTextEdit"/>
							Last Name: <br/><input type="text" id="lastNameTextEdit"/>
							Phone: <br/><input type="text" id="phoneEdit"/>
							Email: <br/><input type="text" id="emailEdit"/>
							Cubicle Number: <br/><input type="text" id="cubicleEdit"/>
							Current Department: <br/><input type="text" id="currentDepartment" disabled="disabled"/>
							Department: <br/><select id="editEmployeeDepartments" style="width:150px;"></select>
						</p>
						<input type="text" id="editHiddenEmployeeID" style="display:none;"/>
						<button value="editEmployee" onclick="editEmployee('0')">Save Edit</button>
						<p id="areYouSureEmployee" style="display:none;">Are You Sure ?</p>
						<button id="sureButtonEditEmployeeYes" style="display:none;" 
							value="editEmployee" onclick="editEmployee('1');setTimeout('location.reload()',200);">Yes</button>
						<button id="sureButtonEditEmployeeNo" style="display:none;" 
							value="editEmployee" onclick="editEmployee('2')">No</button>
					</div>
					<!-- Edit a peripheral's information -->
					<div id="peripheralEdit"  style="display:none;">
						Type: <br/><input type="text" id="peripheralTypeEdit"/>
						<input type="text" id="editHiddenPeripheralID" style="display:none;"/>
						Current Icon: <br/><input type="text" id="currentIcon" disabled="disabled"/>
						Icon: <br/><select id="editPeripheralIcons"></select>
						<button value="editPeripheral" onclick="editPeripheral('0')">Save Edit</button>
						<p id="areYouSurePeripheral" style="display:none;">Are You Sure ?</p>
						<button id="sureButtonEditPeripheralYes" style="display:none;" 
							value="editPeripheral" onclick="editPeripheral('1');setTimeout('location.reload()',200);">Yes</button>
						<button id="sureButtonEditPeripheralNo" style="display:none;" 
							value="editPeripheral" onclick="editPeripheral('2')">No</button>
					</div>
					<br/>
					<p id="editFeature_response"></p>
				</div>
				<!-- Remove an employee -->
				<div id="removeField" style="display:none; height:600px; width:150px;">
					<div id="removeEmployee" style="display:none;">
						<p>
							First Name: <br/><input type="text" id="firstNameTextRemove" disabled="disabled"/>
							Last Name: <br/><input type="text" id="lastNameTextRemove" disabled="disabled"/>
							Phone: <br/><input type="text" id="phoneRemove" disabled="disabled"/>
							Email: <br/><input type="text" id="emailRemove" disabled="disabled"/>
							Cubicle Number: <br/><input type="text" id="cubicleRemove" disabled="disabled"/>
							Current Department: <br/><input type="text" id="currentDepartmentRemove" 
								disabled="disabled"/>
						</p>	
						<input type="text" id="removeHiddenEmployeeID" style="display:none;"/>
						<button value="removeEmployee" onclick="isRemoveEmployee('0')">Remove Feature</button>
						<p id="areYouSureEmployeeRemove" style="display:none;">Are You Sure ?</p>
						<button id="sureButtonRemoveEmployeeYes" style="display:none;" 
							value="removeEmployee" onclick="isRemoveEmployee('1');setTimeout('location.reload()',200);">Yes</button>
						<button id="sureButtonRemoveEmployeeNo" style="display:none;" 
							value="removeEmployee" onclick="isRemoveEmployee('2')">No</button>
					</div>
					<!-- Remove an office peripheral -->
					<div id="peripheralRemove"  style="display:none;">
						Type: <br/><input type="text" id="peripheralTypeRemove" disabled="disabled"/>
						<input type="text" id="removeHiddenPeripheralID" style="display:none;"/>
						Current Icon: <br/><input type="text" id="currentIconRemove" disabled="disabled"/>
						<button value="removePeripheral" onclick="isRemovePeripheral('0')">Remove Feature</button>
						<p id="areYouSurePeripheralRemove" style="display:none;">Are You Sure ?</p>
						<button id="sureButtonRemovePeripheralYes" style="display:none;" 
							value="removePeripheral" onclick="isRemovePeripheral('1');setTimeout('location.reload()',200);">Yes</button>
						<button id="sureButtonRemovePeripheralNo" style="display:none;" 
							value="removePeripheral" onclick="isRemovePeripheral('2')">No</button>
					</div>
					<br/>
					<p id="removeFeature_response"></p>
				</div>
				<!-- Add a new department -->
				<div id="addDepartment" style="display:none; height:600px; width:150px;">
					Department's name: <input type="text" id="newDeptName"/>
					<br/>
					<hr/>
					Select the Icon Color: <select id="iconDropdown"> </select>
					<br/><hr/>
					<button value="Add New Department" onclick="addNewDepartment();setTimeout('location.reload()',200);">Add Department</button>
					<p id="addDepartment_response"></p>
				</div>
				<!-- Remove a department -->
				<div id="removeDepartment" style="display:none; height:600px; width:150px;">
					<select id="removeDepartmentSelect" style="width:150px;"></select>
					<button value="Remove Department" onclick="removeDepartment('0')">Remove Department</button>
					<p id="removeDepartment_response"></p>
					<button id="sureButtonYes" style="display:none;" 
						value="yes" onclick="removeDepartment('1');setTimeout('location.reload()',200);">Yes</button>
					<button id="sureButtonNo" style="display:none;" 
						value="yes" onclick="removeDepartment('2')">No</button>
				</div>
				<div id="insertNew" style="display:none; height:600px; width:150px;">
					<select id="addFeatureSelect" onChange="showInsert(this)">
					<option>Employee</option>
					<option>Peripheral</option></select>
					<!-- Add a new employee -->
					<div id="employeeInsert">
						Employee ID: <input type="text" id ="newEmployeeID"/>
						First Name: <input type="text" id="newFirstName"/>
						Last Name: <input type="text" id="newLastName"/>
						Phone Number(ext): <input type="text" id="newPhone"/>
						Email: <input type="text" id="newEmail"/>
						Cube Number: <input type="text"  id="newCubeNumber"/>
						Department: <select id="departmentSelect" style="width:150px;"></select>
					    <br/><br/>
						<button value="Insert New Object" onclick="insertEmployee();setTimeout('location.reload()',200);">Submit</button>
					</div>
					<!-- Add a new office peripheral -->
					<div id="peripheralInsert" style="display:none;">
						Type: <input type="text" id="newType"/>
						Icon: <select id="iconSelect">
						</select>
						<br/><br/>
						<button value="Insert New Object" onclick="insertPeripheral();setTimeout('location.reload()',200);">Submit</button>
					</div>
						<input type="hidden" id="newFloor" disabled="disabled"/>
						<input type="hidden" id="newLong" disabled="disabled"/>
						<input type="hidden" id="newLat" disabled="disabled"/>
					<br/>
					<p id="insert_response"></p>
				</div>
				<!-- Move something to a new location on the map -->
				<div id="saveMove" style="display:none; height:600px; width:150px;">
					<div id="movePeriphForm"></div>
					<input type="hidden" id="moveLong" />
					<input type="hidden" id="moveLat" />
					<input type="hidden" id="hiddenPeriphID" />
					<input type="hidden" id="hiddenType" />
					<br/>
					<button value="save" onclick="update();setTimeout('location.reload()',200);">Save New Location</button>
					<p id="update_response"></p>
				</div>
			</div>
		</div>
		</div>
		<!-- Floor selection links -->
		<div id="levelFoot">
			<div id="levelSelection"><a href="#" onclick="switchLevel(1)" id="level1"
					style="font-weight:bold; color:#DDD;"
					title="Click here to see the 1st Floor">First Floor </a> |
				<a href="#" onclick="switchLevel(2)" id="level2"
					title="Click here to see the 2nd Floor">Second Floor </a></div>
		</div>
		<div id="footer">	
			<!-- Map options to view or edit different features -->
			<div id="map_options">
				<!-- Radio buttons to choose what admin function is being invoked -->
				<div class="options_title">
					<strong style="text-transform:uppercase;">Map Options</strong>
				</div>
				<div>
					<input type="radio" name="adminoptions" checked onclick="toggleControl(this)"
						value="selectFeature"/>Select Feature
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="addFeature"/>Add Feature
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="removeFeature"/>Remove Feature
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="EditObject"/>Edit Feature
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="move"/>Move Peripheral 
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="addDepartment"/>Add Department
					<input type="radio" name="adminoptions" onclick="toggleControl(this)"
						value="removeDepartment"/>Remove Department
				</div>
			</div>
		</div>
		<div id="display">
				<!-- Check boxes for map display features -->
			<div class="options_title">
				<strong style="text-transform:uppercase;">Display Options</strong>
				<a href="#" onclick="dispOpt()"><img src="img/arrow_down.gif"
					id="display_arrow" title="Click for Display Options" alt="" /></a>
			</div>
			<div id="display_options">
				 <table>
					 <tr>
						 <td><ul>
							 <li id="gold"><input type="checkbox" id="Peripherals" onclick="displaySimpleLayer('peripherals', this.checked)" value="Peripherals"/>Peripherals</li>
							 <li id="lightblue"><input type="checkbox" id="Utilities" onclick="displaySimpleLayer('utilities', this.checked)" value="Utilities"/>Utilities</li>
						 </ul></td>
						 <td><ul>
							 <li id="red"><input type="checkbox" id="Emergency" onclick="displaySimpleLayer('emergencies', this.checked)" value="Emergencies"/>Emergency</li>
							 <li id="purplegreen"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Training Room"/>Training Rooms</li>
						 </ul></td>
						 <td><ul>
							 <li id="magenta"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Classified Lab"/>Classified Labs</li>
							 <li id="green"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Unclassified Lab"/>Unclassified Labs</li>
						 </ul></td>
						 <td><ul>
							 <li id="brown"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Conference Room"/>Conference Rooms</li>
							 <li id="purpleorange"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Pod"/>Pods</li>
						 </ul></td>
						 <td><ul>
							 <li id="purple"><input type="checkbox" id="Pantry" onclick="displaySimpleLayer('pantries', this.checked)" value="Pantries"/>Lunch Rooms</li>
							 <li id="bluepink"><input type="checkbox" name="Room" onclick="displayComplexLayer(this, 'rooms', 'rType')" value="Bathroom"/>Bathrooms</li>
						 </ul></td>
					 </tr>
                 </table>
				<div id="departmentCheckboxes"></div>
			</div>	
		</div>
	</div>
</body>
</html> 
