/*
This javascript file is intended to display an OpenLayers map for MSE
The base picture will be the floors of the MSE Office Building. 

In this file we will grab all the necessary information from an xml web 
page that is generated from a php file.

From there we will draw features on the map based upon their stored
Longitude and lattitude coordinates.

We also will handle all of the user click events in this file
Everything from showing the features specific to a department
To auto focusing on a feature searched for
*/

//Initliaze some fields for the openalyers map
var map;
var mapLevel = 1;
var mapBounds = new OpenLayers.Bounds(-180.0, -167.142857143, 180.0, 90.0);

//This is the levels of zoom the map contains
var mapMinZoom = 0;
var mapMaxZoom = 5;

// avoid pink tiles
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
OpenLayers.Util.onImageLoadErrorColor = "transparent";

/*
This function initiliazes the map as well as displays the appropriate checkboxes for the users
to click to filter the map
*/
function init()
{
    //Set up some optinos for the map, including the number of zooms, the resolution and the controls array that can store various controls for 
    //the map
    var options = {
        controls: [],
        maxExtent: new OpenLayers.Bounds(-180.0, -167.142857143, 180.0, 90.0),
        maxResolution: 1.828571,
        numZoomLevels: 6
    };
    //We now have a map, in the div "map"
    map = new OpenLayers.Map('map', options);
    var layer = new OpenLayers.Layer.TMS("TMS Layer", "", {
        url: '',
        serviceVersion: '.',
        layername: '.',
        alpha: false,
        type: 'jpg',
        getURL: overlay_getTileURL
    });
    layer2 = new OpenLayers.Layer.TMS("TMS Layer", "", {
        url: '',
        serviceVersion: '.',
        layername: '.',
        alpha: false,
        type: 'jpg',
        getURL: overlay_getTileURL2
    });
    layer.id = "level1";
    layer2.id = "level2";
    //add the main layer, this is the office layout pic
    map.addLayer(layer);
    map.addLayer(layer2);
    map.setBaseLayer(layer);
    map.zoomToExtent(mapBounds);
    //we need to add some standard controls including navigation for standard movement throught he map
    map.addControl(new OpenLayers.Control.PanZoomBar());
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.Navigation());
    map.addControl(new OpenLayers.Control.KeyboardDefaults());

    //we need to get the xml of the DB so we can parse it and place it on the map
    var request = OpenLayers.Request.GET({
        url: "createxml.php",
        async: false
    });
    //We get the response xml from the createxml.php page
    var xml = request.responseXML;
    var everything = xml.documentElement;
    
    //We get all the xml items that have the name "icon" and use that data to construct some things on the map
    var icons = everything.getElementsByTagName("icon");

    //for every icon we find from createxml.php we need to add some options to drop down boxes so the admin doesn't enter false info
    for (var i = 0; i < icons.length; i++)
    {
        document.getElementById("iconSelect").innerHTML += "<option>" + icons[i].getAttribute("iconID") + "</option>";
        document.getElementById("iconDropdown").innerHTML += "<option>" + icons[i].getAttribute("iconID") + "</option>";
        document.getElementById("editUtilityIcons").innerHTML += "<option id=\"" + icons[i].getAttribute("iconID") + "Edit\">" + icons[i].getAttribute("iconID") + "</option>";
    }

    //This is our feature vector layer, this is the layer that contains all the markers
    var vector = new OpenLayers.Layer.Vector("features");

    //This sets up event actions
    vector.events.on({
        "beforefeaturemodified": function (event) {
            featurebeingmodified(event);
        },
        "featuremodified": function (event) {
            featuremodified(event);
        }
    });
    vector.id = "features";
    map.addLayer(vector);

    //we set the visibility to false because we wait for the user to select a checkbox
    //that indicates what department they want to view.
    vector.setVisibility(false);

    //stand select control, so when the user clicks a marker
    //it shows them what the marker is
    var control = new OpenLayers.Control.SelectFeature(vector, {
        onSelect: onFeatureSelect,
        onUnselect: onFeatureUnselect,
        onClose: onPopupClose
    });
    control.id = "select";
    map.addControl(control);
    control.activate();

    //this goes through the createxml.php and now sets up all the checkboxes based upon the departments
    var departments = everything.getElementsByTagName("department");
    for(var i = 0; i < departments.length; i++)
    {
        var deptName = "" + departments[i].getAttribute('deptName');
        document.getElementById("checkboxes").innerHTML += "<input type=\"checkbox\" name=\"departments\" onclick=\"showHide()\" value=\"" + deptName + "\" />" + deptName;
        document.getElementById("departmentSelect").innerHTML += "<option value=\"" + deptName + "\" > " + deptName + "</option>";
        document.getElementById("removeDepartmentSelect").innerHTML += "<option value=\"" + deptName + "\" > " + deptName + "</option>";
        document.getElementById("editPersonDepartments").innerHTML += "<option id=\"" + deptName + "Edit\" value=\"" + deptName + "\" > " + deptName + "</option>";
    }

    //we need to parse the xml response, get the attributes, and make a point on the map
    //the point has been styled to be a picture, the reason I am using features, and not markers
    //is because the markers are likely to go extinct, where as the point layer is building steam
    //and has more methods, like pop up bubbles
    //Now we go through all the cubicles from createxml.php and start adding markers to the features layer
    var cubicles = everything.getElementsByTagName("cubicle");
    for(var i = 0; i < cubicles.length; i++)
    {
        var cubicleID = cubicles[i].getAttribute('cubeID');
        var lastName = cubicles[i].getAttribute('lastName');
        var cubicleNumber = cubicles[i].getAttribute('cubeNumber');
        var long = cubicles[i].getAttribute('long');
        var lat = cubicles[i].getAttribute('lat');
        var firstName = cubicles[i].getAttribute('firstName');
        var email = cubicles[i].getAttribute('email');
        var phone = cubicles[i].getAttribute('phone');
        var floor = cubicles[i].getAttribute('floor');
        var deptName = cubicles[i].getAttribute('deptName');
        var personID = cubicles[i].getAttribute('personID');
        var iconName = cubicles[i].getAttribute('iconPath');
        var point = new OpenLayers.LonLat(parseFloat(cubicles[i].getAttribute('lat')), parseFloat(cubicles[i].getAttribute('long')));

        //based upon the longitude and lattitude stored in the db we add a point to the map at that location.
        point = new OpenLayers.Geometry.Point(long, lat);
        feature = new OpenLayers.Feature.Vector(point);
        //add some various attributes to the feature object so we can call upon them later
        feature.lastname = lastName;
        feature.firstname = firstName;
        feature.email = email;
        feature.phone = phone;
        feature.long = long;
        feature.lat = lat;
        feature.deptName = deptName;
        feature.attributes.deptName = deptName;
        feature.iconName = iconName;
        feature.cubicleNumber = cubicleNumber;
        feature.type = "person";
        feature.floor = floor;
        feature.personID = personID;
        feature.attributes.personID = personID; 
        vector.addFeatures(feature);
    }
    //again, we need to initialize all the utilities on the map by parsing the createxml.php response xml
    var utilities = everything.getElementsByTagName("utility");
    for(var i = 0; i < utilities.length; i++)
    {
        var type = utilities[i].getAttribute('type');
        var iconName = utilities[i].getAttribute('iconPath');
        var floor = utilities[i].getAttribute('floor');
        var long = utilities[i].getAttribute('long');
        var lat = utilities[i].getAttribute('lat');
        var utilID = utilities[i].getAttribute('utilID');
        var iconID = utilities[i].getAttribute('iconID');
        var point = new OpenLayers.LonLat(parseFloat(utilities[i].getAttribute('lat')), parseFloat(utilities[i].getAttribute('long')));
        point = new OpenLayers.Geometry.Point(long, lat);

        feature = new OpenLayers.Feature.Vector(point);
        feature.typeAtt = type;
        feature.iconName = iconName;
        feature.iconID = iconID;
        feature.utilID = utilID;
        feature.floor = floor;
        feature.attributes.type = "utilities";
        feature.type = "utility";
        vector.addFeatures(feature);
    }
    //This is a move control, for when the admin wants to move features to a new location.
    var move = new OpenLayers.Control.ModifyFeature(vector);
    map.addControl(move);
    move.id = "move"
    move.mode = OpenLayers.Control.ModifyFeature.DRAG
    map.addControl(move);
    var pointLayer = new OpenLayers.Layer.Vector("Point Layer");
    map.addLayer(pointLayer);
    var draw = new OpenLayers.Control.DrawFeature(pointLayer, OpenLayers.Handler.Point);

    //we add this control so that the admin can draw new points, or new features which could be a utliity or person
    map.addControl(draw);
    draw.id = "addFeature";
    draw.events.on({
        featureadded: function (evt) {
            feature = evt.feature;
            adminInsert(feature);
        }
    });
}

/*
This is the function called after a feature has been moved
Here we just update the movelong and movelat text box values
*/
function featuremodified(event)
{
    if(null != event)
    {
        document.getElementById("moveLong").value = event.feature.geometry.x;
        document.getElementById("moveLat").value = event.feature.geometry.y;
    }
}

/*
This is the function called when the feature is being moved
This shows the feature being modified as well as its values
*/
function featurebeingmodified(event)
{
    var selected = document.getElementById("selected_feature");
    
    //We need to determine what to display based upon whether the feature is a utility or person
    if(null != event && 'utility' == event.feature.type)
    {
        selected.innerHTML = "<form> <p> Utility type <br/> <input type=\"text\" id=\"utiltype\" disabled=\"disabled\" value=\"" + event.feature.typeAtt + "\"/>";
        document.getElementById("saveMove").style.display = "block";
        document.getElementById("moveLong").value = event.feature.geometry.x;
        document.getElementById("moveLat").value = event.feature.geometry.y;
        document.getElementById("hiddenUtilID").value = event.feature.utilID;
        document.getElementById("hiddenType").value = "utility";
    }
    else if(null != event && 'person' == event.feature.type)
    {
        selected.innerHTML = "<form> <p> Personnel <br/>First Name<br/> <input type=\"text\" id=\"First Name\" disabled=\"disabled\" value=\"" + event.feature.firstname + "\"/>" + "<br/>Last Name<br/> <input type=\"text\" id=\"Last Name\" disabled=\"disabled\" value=\"" + event.feature.lastname + "\"/> <br/>Email<br/> <input type=\"text\" id=\"email\" disabled=\"disabled\" value=\"" + event.feature.email + "\"/>";
        document.getElementById("saveMove").style.display = "block";
        document.getElementById("moveLong").value = event.feature.geometry.x;
        document.getElementById("moveLat").value = event.feature.geometry.y;
        document.getElementById("hiddenPersonID").value = event.feature.personID;
        document.getElementById("hiddenType").value = "person";
    }
}

/*
This is the function called when the admin starts to insert a new feature to the map
This function just displays the appropriate html attributes
*/
function adminInsert(feature)
{
    document.getElementById("insertNew").style.display = "block";
    document.getElementById("newLong").value = feature.geometry.x;
    document.getElementById("newLat").value = feature.geometry.y;
    document.getElementById("newFloor").value = mapLevel;
}

//Closes the popup from the user selection
function onPopupClose(evt)
{
    map.getControl("select").unselect(selectedFeature);
}

/*
This function displays the info of a feature when the user clicks on it
This was a requirement by MSE, we open a popup when the user clicks a feature, after we determine if it is a person or utility we display it
If javascript had some kind of on object oriented background it would be appropriate to make a feature interface, and have utility and person
implement the feature interface so they could have appropriate printing methods, however, that is not the case.
*/
function onFeatureSelect(feature)
{
    selectedFeature = feature;
    if('person' == feature.type)
    {
        popup = new OpenLayers.Popup.FramedCloud("something", feature.geometry.getBounds().getCenterLonLat(), null, printPersonBubble(selectedFeature), null, true, onPopupClose);
    }
    else
    {
        popup = new OpenLayers.Popup.FramedCloud("somethingElse", feature.geometry.getBounds().getCenterLonLat(), null, printUtilityBubble(selectedFeature), null, true, onPopupClose);
    }
    feature.popup = popup;
    map.addPopup(popup);
}

/*
If the feature was a person, we need to print the person info into a popup bubble 
*/
function printPersonBubble(feature)
{
    var string = "<p> Name: " + feature.firstname + " " + feature.lastname + "<br/> email: " + feature.email + "<br/> Phone ext: " + feature.phone + "<br/> Department: " + feature['deptName'] + "</p>";
    var buttons = document.getElementsByName("adminoptions");
    var buttonValue;
    for(var i = 0; i < buttons.length; i++)
    {
        if(true == buttons[i].checked)
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
    return string;
}

/*
This adds the HTML to admin.php page when the admin selects a feature to remove 
Just for display purposes
*/
function printRemoveField(feature)
{
    var selected = document.getElementById("removeField");
    selected.style.display = "block";
    if('person' == feature.type)
    {
        document.getElementById("removePerson").style.display = "block";
        document.getElementById("utilityRemove").style.display = "none";
        document.getElementById("firstNameTextRemove").value = feature.firstname;
        document.getElementById("lastNameTextRemove").value = feature.lastname;
        document.getElementById("phoneRemove").value = feature.phone;
        document.getElementById("emailRemove").value = feature.email;
        document.getElementById("cubicleRemove").value = feature.cubicleNumber;
        document.getElementById("currentDepartmentRemove").value = feature.deptName;
        document.getElementById("removeHiddenPersonID").value = feature.personID;
    }
    else if('utility' == feature.type)
    {
        document.getElementById("removePerson").style.display = "none";
        document.getElementById("utilityRemove").style.display = "block";
        document.getElementById("utilityTypeRemove").value = feature.typeAtt;
        document.getElementById("removeHiddenUtilityID").value = feature.utilID;
        document.getElementById("currentIconRemove").value = feature.iconID;
    }
}

/*
This method is called when the admin has clicked the remove feature button
the paramaters are sure which is a boolean that determines whether the admin as click the I'm sure button
Element is the html element being clicked.
*/
function remove(sure, element)
{
    if('removePerson' == element.value)
    {
        if(false == sure)
        {
            document.getElementById("sureButtonRemovePerson").style.display = "block"
            document.getElementById("areYouSurePersonRemove").style.display = "block"
        }
        else
        {
            document.getElementById("sureButtonRemovePerson").style.display = "none"
            document.getElementById("areYouSurePersonRemove").style.display = "none"

            //if The admin is sure he wants to remove this feature, we will call the remove.php page and
            //pass it some variables, the personID so we can remove it fromt he DB
            //This is AJAX
            var personID = document.getElementById("removeHiddenPersonID").value
            http.open('get', 'remove.php?objectType=Person&personID=' + personID);
            http.onreadystatechange = removeReply;
            http.send(null);
        }
    }
    else if('removeUtility' == element.value) 
    {
        if(false == sure)
        {
            document.getElementById("sureButtonRemoveUtility").style.display = "block"
            document.getElementById("areYouSureUtilityRemove").style.display = "block"
        }
        else
        {
            document.getElementById("sureButtonRemoveUtility").style.display = "none"
            document.getElementById("areYouSureUtilityRemove").style.display = "none"
            var utilityID = document.getElementById("removeHiddenUtilityID").value = feature.utilID;

            //if The admin is sure he wants to remove this feature, we will call the remove.php page and
            //pass it some variables, the utilID so we can remove it from the DB
            //This is AJAX
            http.open('get', 'remove.php?objectType=Utility&utilID=' + utilityID);
            http.onreadystatechange = removeReply;
            http.send(null);
        }
    }
}

/*
This displays the HTML on the admin.php page that allows the admin to edit the feature
The parameter is the feature being edited
*/
function printEditField(feature)
{
    var selected = document.getElementById("editField");
    selected.style.display = "block";
    if('person' == feature.type)
    {
        document.getElementById("personEdit").style.display = "block";
        document.getElementById("utilityEdit").style.display = "none";
        document.getElementById("firstNameTextEdit").value = feature.firstname;
        document.getElementById("lastNameTextEdit").value = feature.lastname;
        document.getElementById("phoneEdit").value = feature.phone;
        document.getElementById("emailEdit").value = feature.email;
        document.getElementById("cubicleEdit").value = feature.cubicleNumber;
        document.getElementById("currentDepartment").value = feature.deptName;
        document.getElementById("editHiddenPersonID").value = feature.personID;
    }
    else if('utility' == feature.type)
    {
        document.getElementById("personEdit").style.display = "none";
        document.getElementById("utilityEdit").style.display = "block";
        document.getElementById("utilityTypeEdit").value = feature.typeAtt;
        document.getElementById("editHiddenUtilityID").value = feature.utilID;
        document.getElementById("currentIcon").value = feature.iconID;
    }
}

/*
This is the function that sends the edit request to the edit.php page
The parameters are : Sure - a boolean that verifies the admin wants to edit this feature
                    element- which is the element of the html page being clicked
*/
function edit(sure, element)
{

    if(element.value == "editPerson")
    {

        if (false == sure)
        {
            document.getElementById("sureButtonEditPerson").style.display = "block"
            document.getElementById("areYouSurePerson").style.display = "block"
        }
        else
        {
            //get all the necessary info for the edit
            document.getElementById("sureButtonEditPerson").style.display = "none"
            document.getElementById("areYouSurePerson").style.display = "none"
            var firstName = document.getElementById("firstNameTextEdit").value
            var lastName = document.getElementById("lastNameTextEdit").value
            var phone = document.getElementById("phoneEdit").value
            var email = document.getElementById("emailEdit").value
            var cubicleEdit = document.getElementById("cubicleEdit").value
            var deptName = document.getElementById("editPersonDepartments").options[document.getElementById("editPersonDepartments").selectedIndex].value;
            var personID = document.getElementById("editHiddenPersonID").value

            //This sends the appropriate info to the edit.php page so we can change it in the DB
            http.open('get', 'edit.php?objectType=Person&personID=' + personID + '&firstName=' + firstName + '&lastName=' + lastName + '&phone=' + phone + '&email=' + email + '&cubicle=' + cubicleEdit + '&deptName=' + deptName);
            http.onreadystatechange = editReply;
            http.send(null);
        }
    } 
    else if('editUtility' == element.value)
    {
        if(false == sure)
        {
            document.getElementById("sureButtonEditUtility").style.display = "block"
            document.getElementById("areYouSureUtility").style.display = "block"
        }
        else
        {
            document.getElementById("sureButtonEditUtility").style.display = "none"
            document.getElementById("areYouSureUtility").style.display = "none"
            var utilityType = document.getElementById("utilityTypeEdit").value = feature.typeAtt;
            var utilityID = document.getElementById("editHiddenUtilityID").value = feature.utilID;
            var iconName = document.getElementById("editUtilityIcons").options[document.getElementById("editUtilityIcons").selectedIndex].value;
            //This sends the appropriate info to the edit.php page so we can change it in the DB
            http.open('get', 'edit.php?objectType=Utility&utilID=' + utilityID + '&type=' + utilityType + '&iconName=' + iconName);
            http.onreadystatechange = editReply;
            http.send(null);
        }
    }
}
/*
This prints the bubble if it is a utility feature being selected
the parameter is the feature being selected
*/
function printUtilityBubble(feature)
{
    var string = "<p> Type: " + feature.typeAtt + "</p>";
    var buttons = document.getElementsByName("adminoptions");
    var buttonValue;
    for(var i = 0; i < buttons.length; i++)
    {    
        if(true == buttons[i].checked)
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
    return string;
}
/*
this unselects the feature, which in turn closes the popup
The parameter is againt he feature beign unselected
*/
function onFeatureUnselect(feature)
{
    map.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
}

/*
More functions for initializing the map
*/
function overlay_getTileURL(bounds)
{
    var res = this.map.getResolution();
    var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var z = this.map.getZoom();
    if (x >= 0 && y >= 0)
    {
        return this.url + "1" + z + "/" + x + "/" + y + "." + this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

function overlay_getTileURL2(bounds)
{
    var res = this.map.getResolution();
    var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var z = this.map.getZoom();
    if(x >= 0 && y >= 0) 
    {
        return this.url + "2" + z + "/" + x + "/" + y + "." + this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

/*
The next two functions adjust the map based upont he windowheight, and width
*/
function getWindowHeight()
{
    if(self.innerHeight)
    {
        return self.innerHeight;
    }
    if(document.documentElement && document.documentElement.clientHeight)
    {
        return document.documentElement.clientHeight;
    } 
    if(document.body)
    {
        return document.body.clientHeight;
    } 
    return 0;
}

function getWindowWidth()
{
    if(self.innerWidth)
    {
        return self.innerWidth;
    }
    if(document.documentElement && document.documentElement.clientWidth)
    {
        return document.documentElement.clientWidth;
    }
    if(document.body)
    {
        return document.body.clientWidth;
    }    
    return 0;
}
/*
this changes the map if the user has resized their window
*/
function resize()
{
    var map = document.getElementById("map");
}

/*
this is the feature called when the user clicks a checkbox for display the appropriate features
*/
function showHide()
{
    var departments = document.getElementsByName("departments");
    var utilities = document.getElementById("utilities");
    var layer = map.getLayer("features");

    for(var i = 0; i < departments.length; i++)
    {
        if(departments[i].checked)
        {
            layer.setVisibility(true);
            var features = layer.getFeaturesByAttribute("deptName", departments[i].value);
            
            //We need to find out what departments are checked and display them
            for(var j = 0; j < features.length; j++)
            {
                var iconName = features[j].iconName;
                var level = features[j].floor;
                //if the deparment is checked, then we need to set its radius to 10 and give it it's picture

                if(level == mapLevel)
                {
                    features[j].style = OpenLayers.Util.applyDefaults({
                        externalGraphic: iconName,
                        pointRadius: 10
                    });
                }
                else
                {
                    features[j].style = OpenLayers.Util.applyDefaults({
                        pointRadius: 0
                    });
                }
            }
            layer.redraw();
        } //If the department is not checked we need to set its pointRadius to 0 to make it go away without losing it completely
        else
        {
            var features = layer.getFeaturesByAttribute("deptName", departments[i].value);
            for(var j = 0; j < features.length; j++)
            {
                features[j].style = OpenLayers.Util.applyDefaults({
                    pointRadius: 0
                });
            }
            //redraw refreshes the map
            layer.redraw();
        }
    }
    if(utilities.checked)
    {
        layer.setVisibility(true);
        var features = layer.getFeaturesByAttribute("type", utilities.value);
        //we do the same thing with utilities, if it's checked display them, if its not checked change their radius to 0
        for(var j = 0; j < features.length; j++)
        {
            var iconName = features[j].iconName;
            var level = features[j].floor;
            if(level == mapLevel)
            {
                features[j].style = OpenLayers.Util.applyDefaults({

                    externalGraphic: iconName,
                    pointRadius: 10
                });
            }
            else
            {
                features[j].style = OpenLayers.Util.applyDefaults({
                    pointRadius: 0
                });
            }
        }
        layer.redraw();
    } 
    else
    {
        var features = layer.getFeaturesByAttribute("type", utilities.value);
        for(var j = 0; j < features.length; j++)
        {
            features[j].style = OpenLayers.Util.applyDefaults({
                pointRadius: 0
            });
        }
        layer.redraw();
    }
}

/*
Just a function to clear the edit panel and remove it's inner html because we are no longer editting
*/
function clearEditPanel()
{
    document.getElementById("selected_feature").innerHTML = "";
}

/*
This is the function that handles the clicks on the various admin tools
The parameter is the element being clicked
*/
function toggleControl(element)
{
    clearEditPanel();
    //we perform ceratain operations on the html in admin.php based upon what option is selected
    if('addFeature' == element.value && element.checked)
    {
        map.getControl(element.value).activate();
        document.getElementById("addDepartment").style.display = "none";
        document.getElementById("removeField").style.display = "none";
        document.getElementById("saveMove").style.display = "none";

    }
    else
    {
        map.getControl("addFeature").deactivate();
        document.getElementById("insertNew").style.display = "none";
        document.getElementById("saveMove").style.display = "none";

    }
    if('move' == element.value && element.checked)
    {
        map.getControl("move").activate();
        hideAddDepartment();
        hideRemoveDepartment();
        document.getElementById("removeField").style.display = "none";

    }
    else
    {
        map.getControl("move").deactivate();

    }
    if('addDepartment' == element.value && element.checked)
    {
        showAddDepartment();
        hideRemoveDepartment();
        document.getElementById("removeField").style.display = "none";
        document.getElementById("insertNew").style.display = "none";
        document.getElementById("saveMove").style.display = "none";

    }
    if('removeDepartment' == element.value && element.checked)
    {
        showRemoveDepartment();
        hideAddDepartment();
        document.getElementById("removeField").style.display = "none";
        document.getElementById("insertNew").style.display = "none";
        document.getElementById("saveMove").style.display = "none";
    }
    if('removeFeature' == element.value && element.checked)
    {
        hideRemoveDepartment();
        hideAddDepartment();
        document.getElementById("insertNew").style.display = "none";
        document.getElementById("saveMove").style.display = "none";
    }
    if('selectFeature' == element.value && element.checked)
    {
        //the admin wants to go back to normal selection, so we need to deactivate the other map controls
        map.getControl("move").deactivate();
        //now we need to clear the currently selected div of the page
        document.getElementById("saveMove").style.display = "none";
    }
}

/*
just a collection of showing and hiding html on the admin.php page
*/
function showAddDepartment()
{
    document.getElementById("addDepartment").style.display = "block";
}

function hideAddDepartment()
{
    document.getElementById("addDepartment").style.display = "none";
}

function showRemoveDepartment()
{
    document.getElementById("removeDepartment").style.display = "block";
}

function hideRemoveDepartment()
{
    document.getElementById("removeDepartment").style.display = "none";
}
/*
This shows the approproiate fields if the admin is trying to insert a new object into the DB
the parameter is the the dropdown box element
*/
function showInsert(select)
{
    if (select.options[select.selectedIndex].value == "Employee")
    {
        document.getElementById("employeeInsert").style.display = "block";
        document.getElementById("utilityInsert").style.display = "none";
    } 
    else
    {
        document.getElementById("employeeInsert").style.display = "none";
        document.getElementById("utilityInsert").style.display = "block";
    }
}

/*
This is standard AJAX initialization
*/
function createObject()
{
    var request_type;
    var browser = navigator.appName;
    if (browser == "Microsoft Internet Explorer")
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

/*
This is the function called to update the DB when the admin tries to modify a feature
*/
function update()
{
    document.getElementById('update_response').innerHTML = "Just a second..."
    var utilID = document.getElementById("hiddenUtilID").value;
    var personID = document.getElementById("hiddenPersonID").value;
    var lat = document.getElementById("moveLat").value;
    var long = document.getElementById("moveLong").value;
    var type = document.getElementById("hiddenType").value;
    
    //This is where we send the requrest to update.php to update the DB given the parameters passed to it
    if ('utility' == type)
    {
        http.open('get', 'update.php?objectType=Utility&utilID=' + utilID + '&lat=' + lat + '&long=' + long);
    }
    else if ('person' == type)
    {
        http.open('get', 'update.php?objectType=Person&personID=' + personID + '&lat=' + lat + '&long=' + long);
    }
    http.onreadystatechange = updateReply;
    http.send(null);
}

/*
This function adds a new department to the DB
*/
function addNewDepartment()
{
    document.getElementById('addDepartment_response').innerHTML = "Just a second..."
    var iconID = document.getElementById("iconDropdown").options[document.getElementById("iconDropdown").selectedIndex].value;
    var departmentName = document.getElementById("newDeptName").value;

    //Pass the appropriate parameters to the addDepartment.php page, and this will in turn add it to the DB
    http.open('get', 'addDepartment.php?objectType=Department&iconID=' + iconID + '&departmentName=' + departmentName);
    http.onreadystatechange = addDepartmentReply;
    http.send(null);
}

/*
This function removes the department from the DB
The sure paramater is a check to make sure the admin really wants to remove it
*/
function removeDepartment(sure)
{
    document.getElementById('removeDepartment_response').innerHTML = "Just a second..."
    var deptName = document.getElementById("removeDepartmentSelect").options[document.getElementById("removeDepartmentSelect").selectedIndex].value;
    
    //This makes a called to removeDepartment.php to remove a department, however it also passes in sure, which if it is false
    //removeDepartment.php will return the number of affected rows
    http.open('get', 'removeDepartment.php?departmentName=' + deptName + '&sure=' + sure);
    http.onreadystatechange = removeDepartmentReply;
    http.send(null);
    if(false == sure)
    {
        document.getElementById('sureButton').style.display = "block";
    }
    else
    {
        document.getElementById('removeDepartment_response').innerHTML = "";
        document.getElementById('sureButton').style.display = "none";
    }
}

/*
This function inserts the new object into the DB
*/
function insert()
{
    document.getElementById('insert_response').innerHTML = "Just a second..."

    // Required: verify that all fileds is not empty. 
    var objectType = document.getElementById("addFeatureSelect").options[document.getElementById("addFeatureSelect").selectedIndex].value;
    if(objectType == "Employee")
    {
        var firstName = document.getElementById("newFirstName").value;
        var lastName = document.getElementById("newLastName").value;
        var phone = document.getElementById("newPhone").value;
        var email = document.getElementById("newEmail").value;
        var cubeNumber = document.getElementById("newCubeNumber").value;
        var floor = document.getElementById("newFloor").value;
        var deptName = document.getElementById("departmentSelect").options[document.getElementById("departmentSelect").selectedIndex].value;
       
        var long = document.getElementById("newLong").value;
        var lat = document.getElementById("newLat").value;
        //This is the ajax call to insert.php where we pass in the employee information we are adding
        http.open('get', 'insert.php?objectType=Employee&firstName=' + firstName + '&lastName=' + lastName + '&phone=' + phone + '&email=' + email + '&cubeNumber=' + cubeNumber + '&deptName=' + deptName + '&long=' + long + '&lat=' + lat + '&floor=' + floor);
    }
    else
    {
        var type = document.getElementById("newType").value;
        var iconName = document.getElementById("iconSelect").options[document.getElementById("iconSelect").selectedIndex].value;
        var long = document.getElementById("newLong").value;
        var lat = document.getElementById("newLat").value;
        var floor = document.getElementById("newFloor").value = mapLevel;
        //This is the ajax call to insert.php where we pass in the utility information we are adding.
        http.open('get', 'insert.php?objectType=Utility&type=' + type + '&iconName=' + iconName + '&long=' + long + '&lat=' + lat + '&floor=' + floor);
    }
    http.onreadystatechange = insertReply;
    http.send(null);
}

/*
This prints out the reply from the ajax call to insert
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

/*
This prints out the reply from the ajax call to remove
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

/*
This prints out the reply from the ajax call to edit
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

/*
This prints out the reply from the ajax call to update
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

/*
This prints out the reply from the ajax call to add the department
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

/*
This prints out the reply from the ajax call to remove the department
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

/*
This is a system we use to make sure the admin does not enter any unacceptable characters.
*/
var letters = ' ABCÇDEFGHIJKLMNÑOPQRSTUVWXYZabcçdefghijklmnñopqrstuvwxyz.[\b]';
var email = ' ABCÇDEFGHIJKLMNÑOPQRSTUVWXYZabcçdefghijklmnñopqrstuvwxyz._-@0123456789[\b]';
var numbers = '0123456789[\b]';
var alphanumeral = ' ABCÇDEFGHIJKLMNÑOPQRSTUVWXYZabcçdefghijklmnñopqrstuvwxyz0123456789[\b]';

/*
This function is called everytime a character is typed into the text field, then we check it to make sure it's not
excluded from the appropriate variable of characters.
*/
function alpha(e, allow)
{
    var k;
    k = document.all ? parseInt(e.keyCode) : parseInt(e.which);
    return (allow.indexOf(String.fromCharCode(k)) != -1);
}
function switchLevel(level)
{
    if(1 == level)
    {
        mapLevel = level;
        map.setBaseLayer(map.getLayer("level1"));
        showHide();
        document.getElementById("level1").setAttribute("style", "font-weight:bold; color: #DDD;");
        document.getElementById("level2").setAttribute("style", "font-weight:none; color: #FFF;");
    }
    else
    {
        mapLevel = level;
        map.setBaseLayer(map.getLayer("level2"));
        showHide();
        document.getElementById("level1").setAttribute("style", "font-weight:none; color: #FFF;");
        document.getElementById("level2").setAttribute("style", "font-weight:bold; color: #DDD;");
    }
}
function showSearchSelected(personID)
{
    var layer = map.getLayer("features");
    layer.setVisibility(true);
    showHide();
    var features = layer.getFeaturesByAttribute("personID", personID);
    
    //We need to find out what departments are checked and display them
    for(var j = 0; j < features.length; j++)
    {
        var iconName = features[j].iconName;
        var level = features[j].floor;
        var long = features[j].long;
        var lat = features[j].lat;
        var point = new OpenLayers.LonLat(long, lat);

        //if the deparment is checked, then we need to set its radius to 10 and give it it's picture
        if(level == mapLevel)
        {
            features[j].style = OpenLayers.Util.applyDefaults({
                externalGraphic: iconName,
                pointRadius: 10
            });
            map.panTo(point);
        }
        else 
        {
            if(1 == mapLevel)
            {
                switchLevel(2);
            }
            else
            {
                switchLevel(1);
            }
            features[j].style = OpenLayers.Util.applyDefaults({
                externalGraphic: iconName,
                pointRadius: 10
            });
            map.panTo(point);
        }
    }
    layer.redraw();
}
onresize = function (){
    resize();
};