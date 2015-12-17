/**
 * Authors: Joseph Smith and Christopher Bowen
 *
 * This file provides functionality specific to administrators.
 */

/**
 * Initializes the page to contain additional administrative features.
 */
function adminInit()
{
    var floors =  init();
    var icons = allXML.getElementsByTagName("IconsRow");
    //for every icon we find from createxml.php we need to add some options to drop down boxes so the admin doesn't enter false info
    for(var i = 0; i < icons.length; i++)
    {
        document.getElementById("iconSelect").innerHTML += "<option>" + icons[i].getAttribute("IconID") + "</option>";
        document.getElementById("iconDropdown").innerHTML += "<option>" + icons[i].getAttribute("IconID") + "</option>";
        document.getElementById("editPeripheralIcons").innerHTML += "<option id=\"" + icons[i].getAttribute("IconID") + "Edit\">" + icons[i].getAttribute("IconID") + "</option>";
    }
	
     //this goes through the createxml.php and now sets up all the checkboxes based upon the departments
     var departments = allXML.getElementsByTagName("DepartmentRow");
     for(var i = 0; i < departments.length; i++)
     {
         var deptName = "" + departments[i].getAttribute('DeptName');
		 document.getElementById("departmentSelect").innerHTML += "<option value=\"" + deptName + "\" > " + deptName + "</option>";
         document.getElementById("removeDepartmentSelect").innerHTML += "<option value=\"" + deptName + "\"> " + deptName + "</option>";
         document.getElementById("editEmployeeDepartments").innerHTML += "<option id=\"" + deptName + "Edit\" value=\"" + deptName + "\" > " + deptName + "</option>";
     }

	for(var i = 1; i < floors.length; i++)
	{
		addModify(floors[i]); 
	}
}

/**
 * Adds the ability to move and draw features
 * @param map The map being modified
 */
function addModify(map)
{
	//This is a drag control, for when the admin wants to move features to a new location.
    var move = new OpenLayers.Control.DragFeature(map.getLayer('peripherals'), {
							"onStart": function (feature, pixel){
										onStart(feature, pixel); 
							},
							  "onComplete": function(feature, pixel){
										onComplete(feature, pixel);
							}
	});
    move.id = "move";
	map.addControl(move);
	    
    var pointLayer = new OpenLayers.Layer.Vector("Point Layer");
	pointLayer.id = 'Point Layer';
    map.addLayer(pointLayer);
	pointLayer.setVisibility(false);

    var draw = new OpenLayers.Control.DrawFeature(pointLayer, OpenLayers.Handler.Point);
	
    //we add this control so that the admin can draw new points, or new features which could be a utliity or employee
    map.addControl(draw);
    draw.id = "addFeature";
    draw.events.on({
        featureadded: function (evt) {
            feature = evt.feature;
            adminInsert(feature);
        }
	});
}

/**
 * Method called when a peripheral begins to be moved
 * @param feature The feature being moved
 * @param pixel The pixel location of the feature (filler for OpenLayers)
 */
function onStart(feature, pixel)
{
	document.getElementById('movePeriphForm').innerHTML = "<form id=\"moveForm\"> <strong>Peripheral Type</strong> <br/> \n" +
									"<input type=\"text\" id=\"periphtype\" disabled=\"disabled\" value=\"" + feature.Type + "\"/>";
	document.getElementById("saveMove").style.display = "block";
	document.getElementById("hiddenPeriphID").value = feature.id;
	document.getElementById("hiddenType").value = "peripheral";
}

/**
 * Called when a feature is done being moved
 * @param feature The feature finished moving
 * @param pixel The pixel location of the feature (filler for OpenLayers)
 */
function onComplete(feature, pixel)
{
	document.getElementById("moveLong").value = feature.geometry.x;
	document.getElementById("moveLat").value = feature.geometry.y;
}

/**
 * This is the function called when the admin starts to insert a new feature to the map
 * This function just displays the appropriate html attributes
 * @param feature The feature being added
 */
function adminInsert(feature)
{
    document.getElementById("insertNew").style.display = "block";
    document.getElementById("newLong").value = feature.geometry.x;
    document.getElementById("newLat").value = feature.geometry.y;
    document.getElementById("newFloor").value = mapLevel;
}

/**
 * If the feature was an employee, we need to print the employee info into a popup bubble 
 * @param feature The feature being displayed
 */
function printEmployeeBubble(feature)
{
    var string = "<strong>Name:</strong> " + 
		feature.FirstName + " " + feature.LastName + 
		"<br/> <strong>Email:</strong> " + feature.Email + 
		"<br/> <strong>Phone ext:</strong> " + feature.Phone + 
		"<br/> <strong>Department:</strong> " + feature.DeptName + 
		"<hr/><a href=\'mailto:" + feature.Email + "\' target=\"_blank\"> Email </a></p>";
    var buttons = document.getElementsByName("adminoptions");
    var buttonValue;
    for(var i = 0; i < buttons.length; i++)
    {
        if(buttons[i].checked)
        {
            buttonValue = buttons[i].value;
        }
    }
    
    //Do some checks to see if we are editting, removing, or selecting
    if('EditObject' == buttonValue)
    {
        printEditField(feature);
    }
    if('removeFeature' == buttonValue)
    {
        printRemoveField(feature);
    }
    if('selectFeature' == buttonValue)
    {
		document.getElementById("selected_feature").innerHTML = "<p id=\"selected\">" + string; 
    }
    return "<p>" + string;
}

/**
 * This prints the bubble if it is a peripheral feature being selected
 * @param feature The feature being selected
 */
function printPeripheralBubble(feature)
{
    var string = "<p> Type: " + feature.Type + "</p>";
    var buttons = document.getElementsByName("adminoptions");
    var buttonValue;
    for(var i = 0; i < buttons.length; i++)
    {    
        if(buttons[i].checked)
        {
            buttonValue = buttons[i].value;
        }
    }
    //Do some checks to see if we are editting, removing, or selecting
    if('EditObject' == buttonValue)
    {
        printEditField(feature);
    }
    if('removeFeature' == buttonValue)
    {
        printRemoveField(feature);
    }
	if('selectFeature' == buttonValue)
	{
		document.getElementById("selected_feature").innerHTML = "<p id=\"selected\">" + 
   		"<strong>Type:  </strong>" + feature.Type + "</p>"; 
	}
	return string;
}

/**
 * Displays the form used to remove features 
 * @param feature The feature being removed
*/
function printRemoveField(feature)
{
    var selected = document.getElementById("removeField");
    if('personnel' == feature.attributes.type)
    {
		selected.style.display = "block";
        document.getElementById("removeEmployee").style.display = "block";
        document.getElementById("peripheralRemove").style.display = "none";
        document.getElementById("firstNameTextRemove").value = feature.FirstName;
        document.getElementById("lastNameTextRemove").value = feature.LastName;
        document.getElementById("phoneRemove").value = feature.Phone;
        document.getElementById("emailRemove").value = feature.Email;
        document.getElementById("cubicleRemove").value = feature.CubicleNumber;
        document.getElementById("currentDepartmentRemove").value = feature.DeptName;
        document.getElementById("removeHiddenEmployeeID").value = feature.id;
    }
    else if('peripherals' == feature.attributes.type)
    {
		selected.style.display = "block";
        document.getElementById("removeEmployee").style.display = "none";
        document.getElementById("peripheralRemove").style.display = "block";
        document.getElementById("peripheralTypeRemove").value = feature.Type;
        document.getElementById("removeHiddenPeripheralID").value = feature.id;
        document.getElementById("currentIconRemove").value = feature.IconID;
    }
	else
	{
		selected.style.display = "none";
	}
}

/**
 * Used to hide or display buttons when removing employees
 * @param sure A number value that displays or hides different buttons
*/
function isRemoveEmployee(sure)
{
	if(0 == sure)
    {
        document.getElementById("sureButtonRemoveEmployeeYes").style.display = "block";
		document.getElementById("sureButtonRemoveEmployeeNo").style.display = "block";
        document.getElementById("areYouSureEmployeeRemove").style.display = "block";
    }
    else if(1 == sure)
    {
		document.getElementById("sureButtonRemoveEmployeeNo").style.display = "none";
        document.getElementById("sureButtonRemoveEmployeeYes").style.display = "none";
        document.getElementById("areYouSureEmployeeRemove").style.display = "none";

        //if The admin is sure he wants to remove this feature, we will call the remove.php page and
        //pass it some variables, the employeeID so we can remove it fromt he DB
        //This is AJAX
        var EmployeeID = document.getElementById("removeHiddenEmployeeID").value;
        http.open('get', 'remove.php?objectType=Employee&EmployeeID=' + EmployeeID);
        http.onreadystatechange = removeReply;
        http.send(null);
    }
	else
	{
		document.getElementById("sureButtonRemoveEmployeeYes").style.display = "none";
		document.getElementById("sureButtonRemoveEmployeeNo").style.display = "none";
        document.getElementById("areYouSureEmployeeRemove").style.display = "none";
	}
}

/**
 * Used to hide or display buttons when removing peripherals
 * @param sure A number value that displays or hides different buttons
 */
function isRemovePeripheral(sure)
{
	if(0 == sure)
    {
        document.getElementById("sureButtonRemovePeripheralYes").style.display = "block";
		document.getElementById("sureButtonRemovePeripheralNo").style.display = "block";
        document.getElementById("areYouSurePeripheralRemove").style.display = "block";
    }
    else if(1 == sure)
    {
        document.getElementById("sureButtonRemovePeripheralYes").style.display = "none";
		document.getElementById("sureButtonRemovePeripheralNo").style.display = "none";
        document.getElementById("areYouSurePeripheralRemove").style.display = "none";
	
        var periphID = document.getElementById("removeHiddenPeripheralID").value;

        //if The admin is sure he wants to remove this feature, we will call the remove.php page and
        //pass it some variables, the periphID so we can remove it from the DB
        //This is AJAX
	
        http.open('get', 'remove.php?objectType=Peripheral&periphID=' + periphID);
        http.onreadystatechange = removeReply;
        http.send(null);
    }
	else
	{
		document.getElementById("sureButtonRemovePeripheralYes").style.display = "none";
		document.getElementById("sureButtonRemovePeripheralNo").style.display = "none";
        document.getElementById("areYouSurePeripheralRemove").style.display = "none";
	}
}

/**
 * Displays the form to edit features
 * @param feature The feature being edited
 */
function printEditField(feature)
{
    var selected = document.getElementById("editField");
    
	if('personnel' == feature.attributes.type)
    {
		selected.style.display = "block";
		document.getElementById("employeeEdit").style.display = "block"; 
        document.getElementById("peripheralEdit").style.display = "none";
		document.getElementById("firstNameTextEdit").value = feature.FirstName;
        document.getElementById("lastNameTextEdit").value = feature.LastName;
		document.getElementById("phoneEdit").value = feature.Phone;
        document.getElementById("emailEdit").value = feature.Email;
        document.getElementById("cubicleEdit").value = feature.CubicleNumber;
        document.getElementById("currentDepartment").value = feature.DeptName;
		document.getElementById("editEmployeeDepartments").value = feature.DeptName;
		document.getElementById("editHiddenEmployeeID").value = feature.id;
	}
    else if('peripherals' == feature.attributes.type)
    {
		selected.style.display = "block";
        document.getElementById("employeeEdit").style.display = "none";
        document.getElementById("peripheralEdit").style.display = "block";
        document.getElementById("peripheralTypeEdit").value = feature.Type;
        document.getElementById("editHiddenPeripheralID").value = feature.id;
		document.getElementById("currentIcon").value = feature.IconID;
		document.getElementById("editPeripheralIcons").value = feature.IconID;
    }
	else
	{
		selected.style.display = "none";
	}
}

/**
 * Used to hide or display buttons when saving the edited peripheral information
 * @param sure A number value which will determine what is or isn't displayed on the screen
 */
function editPeripheral(sure)
{
    if(0 == sure)
    {
        document.getElementById("sureButtonEditPeripheralYes").style.display = "block";
		document.getElementById("sureButtonEditPeripheralNo").style.display = "block";
        document.getElementById("areYouSurePeripheral").style.display = "block";
    }
    else if(1 == sure)
    {
        document.getElementById("sureButtonEditPeripheralYes").style.display = "none";
		document.getElementById("sureButtonEditPeripheralNo").style.display = "none";
        document.getElementById("areYouSurePeripheral").style.display = "none";
        var periphType = document.getElementById("peripheralTypeEdit").value;
        var periphID = document.getElementById("editHiddenPeripheralID").value;
        var iconName = document.getElementById("editPeripheralIcons").options[document.getElementById("editPeripheralIcons").selectedIndex].value;
       
		//This sends the appropriate info to the edit.php page so we can change it in the DB
        http.open('get', 'edit.php?objectType=Peripheral&periphID=' + periphID + '&type=' + periphType + '&iconName=' + iconName);
        http.onreadystatechange = editReply;
        http.send(null);
	}
    else
	{
        document.getElementById("sureButtonEditPeripheralYes").style.display = "none";
        document.getElementById("sureButtonEditPeripheralNo").style.display = "none";
        document.getElementById("areYouSurePeripheral").style.display = "none";
	}
}

/**
 * Used to hide or display buttons when saving the edited employee information
 * @param sure A number value which will determine what is or isn't displayed on the screen
 */
function editEmployee(sure)
{
    if(0 == sure)
    {
        document.getElementById("sureButtonEditEmployeeYes").style.display = "block";
		document.getElementById("sureButtonEditEmployeeNo").style.display = "block";
        document.getElementById("areYouSureEmployee").style.display = "block";
    }
    else if(1 == sure)
    {
		//get all the necessary info for the edit
		document.getElementById("sureButtonEditEmployeeYes").style.display = "none";
		document.getElementById("sureButtonEditEmployeeNo").style.display = "none";
		document.getElementById("areYouSureEmployee").style.display = "none";
	
		var firstName = document.getElementById("firstNameTextEdit").value;
	
		var lastName = document.getElementById("lastNameTextEdit").value;
		var phone = document.getElementById("phoneEdit").value;
		var email = document.getElementById("emailEdit").value;
		var cubicleEdit = document.getElementById("cubicleEdit").value;
	
		var deptName = document.getElementById("editEmployeeDepartments").options[document.getElementById("editEmployeeDepartments").selectedIndex].value;
		var EmployeeID = document.getElementById("editHiddenEmployeeID").value;
	
		//This sends the appropriate info to the edit.php page so we can change it in the DB
		http.open('get', 'edit.php?objectType=Employee&EmployeeID=' + EmployeeID + '&firstName=' + firstName + '&lastName=' + lastName + '&phone=' + phone + '&email=' + escape(email) + '&cubicle=' + cubicleEdit + '&deptName=' + escape(deptName));
	
		http.onreadystatechange = editReply;
		http.send(null);
	}
    else 
    {
		document.getElementById("sureButtonEditEmployeeYes").style.display = "none";
		document.getElementById("sureButtonEditEmployeeNo").style.display = "none";
		document.getElementById("areYouSureEmployee").style.display = "none";
    }
}

/**
 * Clear the edit panel and remove it's inner html because we are no longer editting
 */
function clearEditPanel()
{
    document.getElementById("selected_feature").innerHTML = "";
}

/**
 * Hides forms that should not be shown simultaneously
 * @param element The element being clicked
 */
function toggleControl(element)
{
    clearEditPanel();
	
	//we perform ceratain operations on the html in admin.php based upon what option is selected
    if('addFeature' == element.value)
    {
        isInsertNew(true);
    }
    else
    {
        isInsertNew(false);
    }
    if('move' == element.value)
    {
        isMove(true);
    }
    else
    {
        isMove(false);
    }
	if('selectFeature' == element.value)
	{
		isSelectFeature(true);
	}
	else
	{
		isSelectFeature(false);
	}
	if('addDepartment' == element.value)
    {
        isAddDept();
    }
    else if('removeDepartment' == element.value)
    {
        isRemoveDept();
    }
    else if('removeFeature' == element.value)
    {
        isRemoveFeature();
    }
    else if('EditObject' == element.value)
    {
        isEditObject();
    }
}

/**
 * Check to see if insert is active and hide things that should not be shown
 * @param control Activates insert feature
 */
function isInsertNew(control)
{
	if(control)
	{
            map.getControl("addFeature").activate();
			map.getLayer('Point Layer').setVisibility(true);
			document.getElementById("addDepartment").style.display = "none";
            document.getElementById("removeDepartment").style.display = "none";
            document.getElementById("removeField").style.display = "none";
            document.getElementById("saveMove").style.display = "none";
            document.getElementById("editField").style.display = "none";
	}
	else
	{
            map.getControl("addFeature").deactivate();
			map.getLayer('Point Layer').setVisibility(false);
            document.getElementById("insertNew").style.display = "none";
            document.getElementById("saveMove").style.display = "none";
	}
}

/**
 * Check to see if move is active and hide things that should not be shown
 * @param control Activates the move feautre
 */
function isMove(control)
{
	if(control)
	{
			map.getControl("move").activate();
			document.getElementById("addDepartment").style.display = "none";
            document.getElementById("removeDepartment").style.display = "none";
            document.getElementById("removeField").style.display = "none";
            document.getElementById("editField").style.display = "none";
	}
	else
	{
            map.getControl("move").deactivate();
	}
}

/**
 * Check to see if move department is active and hide things that should not be shown
 */
function isAddDept()
{
  
	document.getElementById("addDepartment").style.display = "block";
    document.getElementById("removeDepartment").style.display = "none";
    document.getElementById("removeField").style.display = "none";
    document.getElementById("insertNew").style.display = "none";
    document.getElementById("saveMove").style.display = "none";
    document.getElementById("editField").style.display = "none";
}

/**
 * Check to see if remove department is active and hide things that should not be shown
 */
function isRemoveDept()
{
    document.getElementById("removeDepartment").style.display = "block";
    document.getElementById("addDepartment").style.display = "none";
    document.getElementById("removeField").style.display = "none";
    document.getElementById("insertNew").style.display = "none";
    document.getElementById("saveMove").style.display = "none";
    document.getElementById("editField").style.display = "none";
}

/**
 * Check to see if remove feature is active and hide things that should not be shown
 */
function isRemoveFeature()
{
    document.getElementById("removeDepartment").style.display = "none";
    document.getElementById("addDepartment").style.display = "none";
    document.getElementById("insertNew").style.display = "none";
    document.getElementById("saveMove").style.display = "none";
    document.getElementById("editField").style.display = "none";
}

/**
 * Check to see if select feature is active and hide things that should not be shown
 * @param display Assists in hiding HTML divs that should be hidden
 */
function isSelectFeature(display)
{
	if(!display)
	{
		document.getElementById("selected_feature").style.display = "none";
		return;
	}
	document.getElementById("selected_feature").style.display = "block";
	//the admin wants to go back to normal selection, so we need to deactivate the other map controls
    map.getControl("move").deactivate();
    //now we need to clear the currently selected div of the page
    document.getElementById("saveMove").style.display = "none";
    document.getElementById("editField").style.display = "none";
    document.getElementById("addDepartment").style.display = "none";
    document.getElementById("removeDepartment").style.display = "none";
    document.getElementById("removeField").style.display = "none";
}

/**
 * Hides the other features to display the editing feature
 */
function isEditObject()
{
    document.getElementById("insertNew").style.display = "none";
    document.getElementById("saveMove").style.display = "none";
    document.getElementById("removeField").style.display = "none";
    document.getElementById("removeDepartment").style.display = "none";
    document.getElementById("addDepartment").style.display = "none";
}

/**
 * This shows the appropriate fields if the admin is trying to insert a new object into the DB
 * @param select The dropdown box element
 */
function showInsert(select)
{
    if("Employee" == select.options[select.selectedIndex].value)
    {
        document.getElementById("employeeInsert").style.display = "block";
        document.getElementById("peripheralInsert").style.display = "none";
    } 
    else
    {
        document.getElementById("employeeInsert").style.display = "none";
        document.getElementById("peripheralInsert").style.display = "block";
    }
}

/**
 * This is standard AJAX initialization
 */
function createObject()
{
    var request_type;
    var browser = navigator.appName;
    if("Microsoft Internet Explorer" == browser)
    {
        request_type = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else
    {
        request_type = new XMLHttpRequest();
    }
    return request_type;
}

var http = createObject();
var nocache = 0;

/**
 * Used to initialize a query so the php files can update the database after moving a peripheral
 */
function update()
{
    document.getElementById('update_response').innerHTML = "Just a second...";
    var periphID = document.getElementById("hiddenPeriphID").value;
	var lat = document.getElementById("moveLat").value;
    var long = document.getElementById("moveLong").value;
    var type = document.getElementById("hiddenType").value;
    
    //This is where we send the requrest to update.php to update the DB given the parameters passed to it
    if('peripheral' == type)
    {
       http.open('get', 'update.php?objectType=Peripheral&periphID=' + periphID + 
			'&lat=' + lat + '&long=' + long);
    }
   
    http.onreadystatechange = updateReply;
    http.send(null);
}

/**
 * Used to initialize a query so the php files can update the database after adding a department
 */
function addNewDepartment()
{
    document.getElementById('addDepartment_response').innerHTML = "Just a second...";
    var iconID = document.getElementById("iconDropdown").options[document.getElementById("iconDropdown").selectedIndex].value;
    var departmentName = document.getElementById("newDeptName").value;

    //Pass the appropriate parameters to the addDepartment.php page, and this will in turn add it to the DB
    http.open('get', 'addDepartment.php?objectType=Department&iconID=' + iconID + '&departmentName=' + escape(departmentName));
    http.onreadystatechange = addDepartmentReply;
    http.send(null);
}

/**
 * Used to initialize a query so the php files can update the database after removing a department
 * @param sure Numeric value used to determine buttons to show while removing a department
 */
function removeDepartment(sure)
{
    document.getElementById('removeDepartment_response').innerHTML = "Just a second...";
    var deptName = document.getElementById("removeDepartmentSelect").options[document.getElementById("removeDepartmentSelect").selectedIndex].value;
    
    //This makes a called to removeDepartment.php to remove a department, however it also passes in sure, which if it is false
    //removeDepartment.php will return the number of affected rows
    http.open('get', 'removeDepartment.php?departmentName=' + escape(deptName) + '&sure=' + sure);
    http.onreadystatechange = removeDepartmentReply;
    http.send(null);
    if(0 == sure)
    {
        document.getElementById('sureButtonYes').style.display = "block";
		document.getElementById('sureButtonNo').style.display = "block";
    }
    else if(1 == sure)
    {
        document.getElementById('removeDepartment_response').innerHTML = "";
        document.getElementById('sureButtonYes').style.display = "none";
		document.getElementById('sureButtonNo').style.display = "none";
    }
    else
    {
		document.getElementById('sureButtonYes').style.display = "none";
		document.getElementById('sureButtonNo').style.display = "none";
    }
}

/**
 * Used to initialize a query so the php files can update the database after inserting an employee
 */
function insertEmployee()
{
    document.getElementById('insert_response').innerHTML = "Just a second...";

    // Required: verify that all fields is not empty. 
    var objectType = document.getElementById("addFeatureSelect").options[document.getElementById("addFeatureSelect").selectedIndex].value;
	var employeeID = document.getElementById("newEmployeeID").value;
	var firstName = document.getElementById("newFirstName").value;
    var lastName = document.getElementById("newLastName").value;
	var phone = document.getElementById("newPhone").value;
    var email = document.getElementById("newEmail").value;
	var cubeNumber = document.getElementById("newCubeNumber").value;
    var deptName = document.getElementById("departmentSelect").options[document.getElementById("departmentSelect").selectedIndex].value;

    //This is the ajax call to insert.php where we pass in the employee information we are adding
    http.open('get', 'insert.php?objectType=Employee&employeeID=' + employeeID + '&firstName=' + firstName + '&lastName=' + lastName + '&phone=' + phone + '&email=' + escape(email) + '&cubeNumber=' + cubeNumber + '&deptName=' + escape(deptName));
    http.onreadystatechange = insertReply;
    http.send(null);
}

/**
 * Used to initialize a query so the php files can update the database after inserting a peripheral
 */
function insertPeripheral()
{
    document.getElementById('insert_response').innerHTML = "Just a second...";

    // Required: verify that all fileds is not empty. 
    var objectType = document.getElementById("addFeatureSelect").options[document.getElementById("addFeatureSelect").selectedIndex].value;
    var type = document.getElementById("newType").value;
    var iconName = document.getElementById("iconSelect").options[document.getElementById("iconSelect").selectedIndex].value;
    var longitude = document.getElementById("newLong").value;
    var latitude = document.getElementById("newLat").value;
    var floor = document.getElementById("newFloor").value = mapLevel;
    //This is the ajax call to insert.php where we pass in the peripheral information we are adding.
    http.open('get', 'insert.php?objectType=Peripheral&type=' + type + '&iconName=' + iconName + '&long=' + longitude + '&lat=' + latitude + '&floor=' + floor);
	http.onreadystatechange = insertReply;
    http.send(null);
}

/**
 * Prints out the reply from the ajax call to insert
 */
function insertReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('insert_response').innerHTML =  response;
    }
}

/**
 * Prints out the reply from the ajax call to remove
 */
function removeReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('removeFeature_response').innerHTML = 'Object removed:' + response;
    }
}

/**
 * Prints out the reply from the ajax call to edit
 */
function editReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('editFeature_response').innerHTML = response;
    }
}

/**
 * Prints out the reply from the ajax call to update
 */
function updateReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('update_response').innerHTML = 'Updated Item:' + response;
    }
}

/**
 * Prints out the reply from the ajax call to add the department
 */
function addDepartmentReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('addDepartment_response').innerHTML = response;
    }
}

/**
 * Prints out the reply from the ajax call to remove the department
 */
function removeDepartmentReply()
{
    if(4 == http.readyState)
    {
        var response = http.responseText;
        // else if login is ok show a message: "Site added+ site URL".
        document.getElementById('removeDepartment_response').innerHTML = response;
    }
} 

/**
 * Displays the info of a feature when the user clicks on it
 * After we determine if it is an employee or another feature we display it
 * @param feature The feature being selected
 */
function onFeatureSelect(feature)
{
    selectedFeature = feature;
	document.getElementById('saveMove').style.display = "none";
    popup = new OpenLayers.Popup.FramedCloud('', feature.geometry.getBounds().getCenterLonLat(), null, (feature.attributes.type == 'personnel') ? printEmployeeBubble(selectedFeature) : printPeripheralBubble(selectedFeature), null, true, onPopupClose);
    feature.popup = popup;
    map.addPopup(popup);
}

/**
 * Unselects a feature and destroys its on-map display
 * @param feature The feature being unselected
 */
function onFeatureUnselect(feature)
{
    selectedFeature = null;
    map.removePopup(feature.popup);
    document.getElementById("selected_feature").innerHTML = '[Nothing selected]';
    feature.popup.destroy();
    feature.popup = null;
	hideSelected();
}

/**
 * Hide form elements when a feature is unselected
 */
function hideSelected()
{
	document.getElementById('editField').style.display = "none";
	document.getElementById('removeField').style.display = "none";
	document.getElementById('insertNew').style.display = "none";
    document.getElementById('saveMove').style.display = "none";
} 

