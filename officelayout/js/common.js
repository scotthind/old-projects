/**
 *Authors: Joseph Smith and Christopher Bowen
 *
 *This file exists to provide the functionality that is common between
 *regular users and administrators.
 */

// The map we are marking and navigsting
var map;
// The floor that is currently being displayed
var mapLevel = 1;
// The number of floors in the building
var numLevels = 2;
//An array of floormaps
var floors = new Array();  // I should set the size with num levels... but I want to start positioning at index 1, so that's numLevels + 1...
// The XML representation of the data stored in the backend
var allXML;
// The currently selected feature on the map
var selectedFeature;

// avoid pink tiles (graphical errors)
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
OpenLayers.Util.onImageLoadErrorColor = "transparent";

/**
 * This function initializes the map and displays the appropriate options and
 * controls to the user.
 */
function init()
{
    // Get the XML representation of the contents of the database backend
    // so that we may parse it and display the appropriate features on the map.

   var request = OpenLayers.Request.GET({
                                        url: "createxml.php",
                                        async: false
                                        });
    // Get the response XML from the createxml.php page
	allXML = request.responseXML.documentElement;

    var bounds = new OpenLayers.Bounds(-180.0, -167.142857143, 180.0, 90.0);

    //Initialize the first floor map
    var floor = map = new OpenLayers.Map('map', { controls: [], 
                                      maxExtent: bounds,
                                      maxResolution: 1.828571,
                                      numZoomLevels: 6 });
    var layer = new OpenLayers.Layer.TMS("TMS Layer", "", {
                        url: '',
                        serviceVersion: '.',
                        layername: '.',
                        alpha: false,
                        type: 'jpg',
                        getURL: overlay_getTileURL //if it wasn't for THIS I could do this in a loop
                        });                        //we're passing in the name of the function that will override getURL
	initializeFloor(floor, layer, 1);              //concatenation is not an option here
    floor.events.register('zoomend', this, function(event) {
        if(map.getZoom() < 2)
        {
            map.zoomTo(2);
        }
    });
    floors[1] = floor;

    //Initialize the second floor map
    floor = new OpenLayers.Map( { controls: [], 
                                 maxExtent: bounds,
                                 maxResolution: 1.828571,
                                 numZoomLevels: 6 });
    layer = new OpenLayers.Layer.TMS("TMS Layer", "", {
                        url: '',
                        serviceVersion: '.',
                        layername: '.',
                        alpha: false,
                        type: 'jpg',
                        getURL: overlay_getTileURL2
                        });
    initializeFloor(floor, layer, 2);
    floor.events.register('zoomend', this, function(event) {
        if(map.getZoom() < 2)
        {
            map.zoomTo(2);
        }
    });
    floors[2] = floor;

	initializeDisplayOptions(allXML.getElementsByTagName("DepartmentRow"));

	return floors;
}

/**
 * Initialiezes all of the department checkboxes used to display
 * icons of employees who are assigned to the department(s) selected
 * @departments A list of the departments in the database
 */
function initializeDisplayOptions(departments)
{
	var inHTML = "\n                        <table>\n";
    inHTML += "                      <tr>\n";
    inHTML += "\n                           <td><ul>\n";

    for(var i = 1; i <= departments.length; i++)
    {
        //This just puts all the appropriate checkboxes at the bottom of the screen for display purposes
        inHTML += "                                     "
                   + "<li style=\"list-style-image:url('" + departments[i-1].getAttribute('IconPath') + "');\"><input type=\"checkbox\" name=\"departments\" onclick=\"displayComplexLayer(this, \'personnel\', \'DeptName\')\" value=\""
                                                        + departments[i-1].getAttribute('DeptName') + "\" />"
                                                        + departments[i-1].getAttribute('DeptName') + "</li>\n";
        if(i % 4  == 0)
        {
           inHTML += "                     </ul></td>\n";
           if(i % 12 == 0)
           {
              inHTML += "                     </tr>\n                 <tr>\n";
           }
           inHTML += "                     <td><ul style=\"list-style:none;\">\n";
        }
    }
    inHTML += "                     </ul></td>\n";
    inHTML += "                     </tr>\n";
    inHTML += "                </table>\n";

    document.getElementById("departmentCheckboxes").innerHTML =  inHTML;
}

/**
 * This function initializes a map. It fetches all relevant information
 * from an XML representation of the database.
 * @map The map to be populated with layers.
 * @baseLayer The layer upon which features will be displayed.
 * @level The floor the map represents.
 */
function initializeFloor(map, baseLayer, level)
{
    baseLayer.id = 'level' + level;
    map.addLayer(baseLayer);
    map.setBaseLayer(baseLayer);
    map.zoomToMaxExtent();
    
    map.addControl(new OpenLayers.Control.PanZoomBar());
//  map.addControl(new OpenLayers.Control.MousePosition()); //Enables coordinates at the bottom of the map
    map.addControl(new OpenLayers.Control.Navigation());
    
    var vectorLayer = initializeRooms(allXML.getElementsByTagName("RoomFloor" + level + "Row"));
	map.addLayer(vectorLayer); 
	vectorLayer.setVisibility(true);
    
    vectorLayer = initializePantries(allXML.getElementsByTagName("PantryFloor" + level + "Row"));
    map.addLayer(vectorLayer);
    vectorLayer.setVisibility(false);

    vectorLayer = initializePersonnel(allXML.getElementsByTagName("EmployeeFloor" + level + "Row"));
    map.addLayer(vectorLayer);
    vectorLayer.setVisibility(true);
    
    vectorLayer = initializePeripherals(allXML.getElementsByTagName("PeripheralsFloor" + level + "Row"));
    map.addLayer(vectorLayer);
    vectorLayer.setVisibility(false);
    
    vectorLayer = initializeUtilities(allXML.getElementsByTagName("UtilitiesFloor" + level + "Row"));
	map.addLayer(vectorLayer);
    vectorLayer.setVisibility(false);
    
    vectorLayer = initializeEmergency(allXML.getElementsByTagName("EmergencyFloor" + level +"Row"));
	map.addLayer(vectorLayer);
    vectorLayer.setVisibility(false);
    
    var control = new OpenLayers.Control.SelectFeature([map.getLayer('personnel'), map.getLayer('peripherals'), map.getLayer('utilities'), map.getLayer('emergencies')], {
                                                        onSelect: onFeatureSelect,
                                                        onUnselect: onFeatureUnselect,
                                                        onClose: onPopupClose
                                                        });
    control.id = "selectFeatures";
    map.addControl(control);
    control.activate();
}

// All the initialize methods below here should be able to be rolled into one
// -- look into parsing XML DOM objects with OpenLayers. I know it can be done -- 
// I read about it -- just don't know how.

/**
 * This function initializes emergencies and places their markers on the map.
 * @emergencies A collection of emergencies objects from the XML.
 */ 
function initializeEmergency(emergencies)
{
    var emergencyVector = new OpenLayers.Layer.Vector('emergencies');
    emergencyVector.id = 'emergencies';
    var feature;

    for(var i = 0; i < emergencies.length; i++)
    {
        feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(emergencies[i].getAttribute('Longitude'), emergencies[i].getAttribute('Latitude')));
        feature.id = 'e' + emergencies[i].getAttribute('EmergencyID');
        feature.IconID = emergencies[i].getAttribute('IconID');
        feature.IconPath = emergencies[i].getAttribute('IconPath');
        feature.Type = emergencies[i].getAttribute('Type');
        feature.Longitude = emergencies[i].getAttribute('Longitude');
        feature.Latitude = emergencies[i].getAttribute('Latitude');
        feature.Floor = emergencies[i].getAttribute('Floor');
        feature.attributes.type = "emergencies";
        feature.style = OpenLayers.Util.applyDefaults({
                                   externalGraphic: feature.IconPath,
                                   pointRadius: 10
                                });
        emergencyVector.addFeatures(feature);
    }
    return emergencyVector;
}

/**
 * This function initializes personnel and places their markers on the map.
 * @personnel A collection of personnel objects from the XML.
 */
function initializePersonnel(personnel)
{
    var personnelVector = new OpenLayers.Layer.Vector('personnel');
    personnelVector.id = 'personnel';
    var feature;

    for(var i = 0; i < personnel.length; i++)
    {
        feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(personnel[i].getAttribute('Longitude'), personnel[i].getAttribute('Latitude')));
        feature.id = personnel[i].getAttribute('EmployeeID');
        feature.CubicleNumber = personnel[i].getAttribute('CubicleNumber');
        feature.FirstName = personnel[i].getAttribute('FirstName');
        feature.LastName = personnel[i].getAttribute('LastName');
        feature.DeptName = personnel[i].getAttribute('DeptName');
        feature.Email = personnel[i].getAttribute('Email');
        feature.Phone = personnel[i].getAttribute('Phone');
        feature.Latitude = personnel[i].getAttribute('Latitude');
        feature.Longitude = personnel[i].getAttribute('Longitude');
        feature.Floor = personnel[i].getAttribute('Floor');
        feature.IconID = personnel[i].getAttribute('IconID');
        feature.IconPath = personnel[i].getAttribute('IconPath');
		feature.attributes.DeptName = feature.DeptName;
        feature.attributes.type = "personnel";
        feature.style = OpenLayers.Util.applyDefaults({
                                   externalGraphic: feature.IconPath,
                                   pointRadius: 0
                                });
        personnelVector.addFeatures(feature);i
		
    }
	return personnelVector;
}

/**
 * This function initializes utilities and places their markers on the map.
 * @utilities A collection of utilities objects from the XML.
 */
function initializeUtilities(utilities)
{
    var utilitiesVector = new OpenLayers.Layer.Vector('utilities');
    utilitiesVector.id = 'utilities';
    var feature;
 
    for(var i = 0; i < utilities.length; i++)
    {
        feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(utilities[i].getAttribute('Longitude'), utilities[i].getAttribute('Latitude')));
        feature.id = 'u' + utilities[i].getAttribute('UtilID');
        feature.Type = utilities[i].getAttribute('Type');
        feature.IconID = utilities[i].getAttribute('IconID');
        feature.IconPath = utilities[i].getAttribute('IconPath');
        feature.Longitude = utilities[i].getAttribute('Longitude');
        feature.Latitude = utilities[i].getAttribute('Latitude');
        feature.Floor = utilities[i].getAttribute('Floor');
        feature.attributes.type = "utilities";
        feature.style = OpenLayers.Util.applyDefaults({
                                   externalGraphic: feature.IconPath,
                                   pointRadius: 10
                                });
        utilitiesVector.addFeatures(feature);
    }
    return utilitiesVector;
}

/**
 * This function initializes pantries and places their markers on the map.
 * @pantries A collection of pantry objects from the XML.
 */
function initializePantries(pantries)
{
    var pantryVector = new OpenLayers.Layer.Vector('pantries');
    pantryVector.id = 'pantries';
    var feature;
  
    for(var i = 0; i < pantries.length; i++)
    {
        feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(pantries[i].getAttribute('Longitude'), pantries[i].getAttribute('Latitude')));
        feature.id = 'p' + pantries[i].getAttribute('PantryID');
        feature.IconID = pantries[i].getAttribute('IconID');
		feature.Type = pantries[i].getAttribute('Type');
        feature.IconPath = pantries[i].getAttribute('IconPath');
        feature.Longitude = pantries[i].getAttribute('Longitude');
        feature.Latitude = pantries[i].getAttribute('Latitude');
        feature.Floor = pantries[i].getAttribute('Floor');
        feature.attributes.type = "pantries";
        feature.style = OpenLayers.Util.applyDefaults({
                                   externalGraphic: feature.IconPath,
                                   pointRadius: 10
                                });
        pantryVector.addFeatures(feature);
    }
    return pantryVector;
}

/**
 * This function initializes rooms and places their markers on the map.
 * @rooms A collection of room objects from the XML.
 */
function initializeRooms(rooms)
{
	var roomVector = new OpenLayers.Layer.Vector("rooms");
	roomVector.id = 'rooms';
	var feature;
	
	for(var i = 0; i < rooms.length; i++)
	{
		feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(rooms[i].getAttribute('Longitude'), rooms[i].getAttribute('Latitude')));
		feature.id = 'r' + rooms[i].getAttribute('RoomID');
		feature.Type = rooms[i].getAttribute('Type');
		feature.IconID = rooms[i].getAttribute('IconID');
        feature.IconPath = rooms[i].getAttribute('IconPath');
		feature.Floor = rooms[i].getAttribute('Floor');
		feature.Longitude = rooms[i].getAttribute('Longitude');
		feature.Latitude = rooms[i].getAttribute('Latitude');
        feature.attributes.type = "rooms";
		feature.attributes.rType = feature.Type;
        feature.style = OpenLayers.Util.applyDefaults({
                                        externalGraphic: feature.IconPath,
                                        pointRadius: 0
                                });
		roomVector.addFeatures(feature);
	}
	return roomVector;
}

/**
 * This function initializes peripherals and places their markers on the map.
 * @peripherals A collection of peripheral objects from the XML.
 */
function initializePeripherals(peripherals)
{
	var peripheralsVector = new OpenLayers.Layer.Vector("peripherals");
	peripheralsVector.id = 'peripherals';
	var feature;
	
	for(var i = 0; i < peripherals.length; i++)
	{
		feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(peripherals[i].getAttribute('Longitude'), peripherals[i].getAttribute('Latitude')));
		feature.id = peripherals[i].getAttribute('PeriphID');
		feature.Type = peripherals[i].getAttribute('Type');
		feature.IconID = peripherals[i].getAttribute('IconID');
        feature.IconPath = peripherals[i].getAttribute('IconPath');
		feature.Floor = peripherals[i].getAttribute('Floor');
		feature.Longitude = peripherals[i].getAttribute('Longitude');
		feature.Latitude = peripherals[i].getAttribute('Latitude');
        feature.attributes.type = "peripherals";
        feature.style = OpenLayers.Util.applyDefaults({
                                        externalGraphic: feature.IconPath,
                                        pointRadius: 10
                                });
		peripheralsVector.addFeatures(feature);
	}
	return peripheralsVector;
}

/*
 * This function displays or hides feature markers on the map,
 * according to whether the relevant checkboxes have been checked.
 */
function showHide() 
{
	displaySimpleLayer('peripherals', document.getElementById('Peripherals').checked);
    displaySimpleLayer('utilities', document.getElementById('Utilities').checked);
    displaySimpleLayer('emergencies', document.getElementById('Emergency').checked);
    displaySimpleLayer('pantries', document.getElementById('Pantry').checked);
	displayComplexLayer(document.getElementsByName('departments'), 'personnel', 'DeptName');
	displayComplexLayer(document.getElementsByName('Room'), 'rooms', 'rType');
}

/**
 * This function is called when a layer consists of objects that are different enough
 * so that we might sometimes want to display only some of them.
 * @checkboxes The checkboxes for the different categories of elements in the complex layer. 
 */
function displayComplexLayer(checkboxes, layer, attribute)
{
    var mapLayer = map.getLayer(layer);
	if(!(Object.prototype.toString.call(checkboxes) === '[object Array]' || Object.prototype.toString.call(checkboxes) === '[object HTMLCollection]'))
	{
		checkboxes = [ checkboxes ]; 
	}
    for(var i = 0; i < checkboxes.length; i++)
    {
		displayComplexFeatures(mapLayer.getFeaturesByAttribute(attribute, checkboxes[i].value), checkboxes[i].checked);
    }

    mapLayer.redraw();

}

/**
 * Draw elements of a complex layer onto the map.
 * Depending on the value of display, they will either become visible or disappear.
 * @features The features being displayed
 * @display Boolean to determine if the feature is being displayed
 */
function displayComplexFeatures(features, display)
{
    var i;
    if(display)
    {
		for(i = 0; i < features.length; i++)
		{
            console.log(features[i]);
            features[i].style = OpenLayers.Util.applyDefaults( {externalGraphic: features[i].IconPath, pointRadius: 10} );
        }
        return;
    }
    for(i = 0; i < features.length; i++)
    {
		features[i].style = OpenLayers.Util.applyDefaults( {pointRadius: 0} );
    }
}

//Will work for pantries and peripherals, at the very least.
//Same with utilities.
//Also emergency if we don't discriminate between equipment and escapes.
function displaySimpleLayer(layer, display)
{
	map.getLayer(layer).setVisibility(display);
}

/**
 * This function is designed to set the map display to the appropriate floor.
 * @level The floor to be displayed.
 */
function switchLevel(level)
{
    if(mapLevel == level)
    {
        return;
    }
    mapLevel = level;
	map.render('otherfloor');	
	map = floors[mapLevel];
    map.render('map');

	for(var i = 1; i <= numLevels; i++)
    {
		document.getElementById("level" + i).setAttribute("style", (level == i) ? "font-weight:bold; color: #DDD;" : "font-weight:none; color: #FFF;");
    }
	showHide();
}

/**
 * Loads in the images for the first floor tiles
 */
function overlay_getTileURL(bounds)
{
    var res = this.map.getResolution();
    var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var z = this.map.getZoom();
    if (x >= 0 && y >= 0 && y != 17)
    {
        return this.url + "FloorTile/Floor1/" + "1" + z + "/" + x + "/" + y + "." + this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

/**
 * Loads in the images for the second floor tiles
 */
function overlay_getTileURL2(bounds)
{
    var res = this.map.getResolution();
    var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var y = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var z = this.map.getZoom();
    if(x >= 0 && y >= 0 && y != 17) 
    {
        return this.url + "FloorTile/Floor2/" + "2" + z + "/" + x + "/" + y + "." + this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

/**
 * Used when displaying search results. If a search result is selected,
 * the map pans to the location of the selected item and displays the
 * popup bubble for the employee selected.
 * @employeeID The employee ID of the selected search result
 */
function showSearchSelected(employeeID)
{
	var i = 1, feature, layer;
	while(null == feature && i <= numLevels)
	{
		layer = floors[i++].getLayer('personnel');
		feature = layer.getFeatureById(employeeID);
	}
	if(null == feature)
	{
		return;
	}
	switchLevel(--i);
	displayComplexLayer(document.getElementsByName('departments'), 'personnel', 'DeptName');
	feature.style = OpenLayers.Util.applyDefaults( {externalGraphic: feature.IconPath, pointRadius: 10} );
	map.panTo(new OpenLayers.LonLat(feature.Longitude, feature.Latitude));
	if(null != selectedFeature)
	{
		onFeatureUnselect(selectedFeature);
	}
	onFeatureSelect(feature);
    layer.redraw();
}

/**
 * Called when a popup window is closed
 * @evt The event of the popup window closing (filler for OpenLayers)
 */
function onPopupClose(evt)
{
	map.getControl("selectFeatures").unselect(selectedFeature);
}

/**
 * Adds a dropdown effect to the display options on the page
 */
function dispOpt() {
    $("#display_options").slideToggle();
} 

