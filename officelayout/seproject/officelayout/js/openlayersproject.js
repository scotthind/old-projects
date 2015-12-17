/*
This JavaScript file is intended to display an OpenLayers map for MSE.
The base picture will be the floors of the MSE Office Building.

In this file we will grab all the necessary information from a XML web
page that is generated from a PHP file.

From there, we will draw features on the map based upon their stored
longitude and latitude coordinates.

We also will handle all of the user click events in this file --
everything from showing the features specific to a department
to auto-focusing on a feature that was searched for.
*/

//Initialize some fields for the OpenLayers map
var map;
var mapBounds = new OpenLayers.Bounds(-180.0, -167.142857143, 180.0, 90.0);

//These are the zoom levels for the map
var mapMinZoom = 0;
var mapMaxZoom = 5;
var mapLevel = 1;

// avoid pink tiles (graphical errors)
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
OpenLayers.Util.onImageLoadErrorColor = "transparent";

/*
This function initializes the map and displays the appropriate checkboxes to
filter the map
*/
function init()
{
    //Set up some options for the map, including the number of zooms,
    //the resolution and various controls for the map
    var options = 
        {
            controls: [],
            maxExtent: new OpenLayers.Bounds(-180.0, -167.142857143, 180.0, 90.0),
            maxResolution: 1.828571,
            numZoomLevels: 6
        };

    //We now have a map, in the div "map"
    map = new OpenLayers.Map('map', options);
    
    layer = new OpenLayers.Layer.TMS("TMS Layer", "", {
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

    var vector = new OpenLayers.Layer.Vector("features");
    vector.events.on({
                    "beforefeaturemodified": function (event) {
                                            featurebeingmodified(event);
                    },
                    "featuremodified": function (event) {
                                            featuremodified(event);
                    }
                    });
    //This is our feature vector layer, this is the layer that contains all the markers
    vector.id = "features";
    //we set the visibility to false because we wait for the user to select a checkbox
    //that indicates what department they want to view.
    vector.setVisibility(false);
    map.addLayer(vector);
    
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
        //This just put's all the appropriate checkboxes at the bottom of the screen for display purposes
        document.getElementById("checkboxes").innerHTML += "<input type=\"checkbox\" name=\"departments\" onclick=\"showHide()\" value=\"" 
                                                        + departments[i].getAttribute('deptName') + "\" />" 
                                                        + departments[i].getAttribute('deptName');
    }

    //we need to parse the xml response, get the attributes, and make a point on the map
    //the point has been styled to be a picture, the reason I am using features, and not markers
    //is because the markers are likely to go extinct, where as the point is building steam
    //and has more methods, like pop up bubbles
    //Now we go through all the cubicles from createxml.php and start adding markers to the features layer
    var cubicles = everything.getElementsByTagName("cubicle");
    for(var i = 0; i < cubicles.length; i++)
    {
        var cubicleID = cubicles[i].getAttribute('cubeID');
        var cubicleNumber = cubicles[i].getAttribute('cubeNumber');
        var point = new OpenLayers.LonLat(parseFloat(cubicles[i].getAttribute('lat')), parseFloat(cubicles[i].getAttribute('long')));
        point = new OpenLayers.Geometry.Point(cubicles[i].getAttribute('long'), cubicles[i].getAttribute('lat'));

        feature = new OpenLayers.Feature.Vector(point);
        //based upon the longitude and lattitude stored in the db we add a point to the map at that location.
        
        //this is where we do any HTML formatting to make the popup bubble look nice in our description text
        //this variable is needed when the feature is selected
        //add some various attributes to the feature object so we can call upon them later
        feature.firstname = cubicles[i].getAttribute('firstName');
        feature.lastname = cubicles[i].getAttribute('lastName');
        feature.long = cubicles[i].getAttribute('long');
        feature.lat = cubicles[i].getAttribute('lat');
        feature.email = cubicles[i].getAttribute('email');
        feature.phone = cubicles[i].getAttribute('phone');
        feature.deptName = feature.attributes.deptName = cubicles[i].getAttribute('deptName');
        feature.id = feature.attributes.personID = cubicles[i].getAttribute('personID');
        feature.iconName = cubicles[i].getAttribute('iconPath');
        feature.type = "person";
        feature.floor = cubicles[i].getAttribute('floor');
        vector.addFeatures(feature);
    }
    
    //again, we need to iniliaze all the utilities on the map by parsing the createxml.php response xml
    var utilities = everything.getElementsByTagName("utility");
    for(var i = 0; i < utilities.length; i++)
    {
        var point = new OpenLayers.LonLat(parseFloat(utilities[i].getAttribute('lat')), parseFloat(utilities[i].getAttribute('long')));
        point = new OpenLayers.Geometry.Point(utilities[i].getAttribute('long'), utilities[i].getAttribute('lat'));
        feature = new OpenLayers.Feature.Vector(point);
        feature.typeAtt = utilities[i].getAttribute('type');
        feature.iconName = utilities[i].getAttribute('iconPath');
        feature.iconID = utilities[i].getAttribute('iconID');
        feature.utilID = utilities[i].getAttribute('utilID');
        feature.floor = utilities[i].getAttribute('floor');
        feature.attributes.type = "utilities";
        feature.type = "utility";
        vector.addFeatures(feature);
    }
}
/*
This prints the bubble if it is a utility feature being selected
the parameter is the feature being selected
*/
function printUtilityBubble(feature)
{
    return (document.getElementById("selected_feature").innerHTML = "<p> Type: " + feature.typeAtt + "</p>");
}
/*
If the feature was a person, we need to print the person info into a popup bubble 
*/
function printPersonBubble(feature)
{
    return (document.getElementById("selected_feature").innerHTML = "<p> <strong>Name:  </strong>" + feature.firstname + " " + feature.lastname + "<br/> <strong>Phone ext:  </strong>" +
                                                            feature.phone + "<br/><strong>Department:   </strong> "+feature.deptName + "<hr/><a href=\'mailto:" + feature.email+"\'\"> Email </a></p>");
}
//This closes the popup, based upon the event that  took place
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
    if('person' == feature.type)
    {
        popup = new OpenLayers.Popup.FramedCloud("something", feature.geometry.getBounds().getCenterLonLat(), null, printPersonBubble(feature), null, true, onPopupClose);
    }
    else
    {
        popup = new OpenLayers.Popup.FramedCloud("somethingElse", feature.geometry.getBounds().getCenterLonLat(), null, printUtilityBubble(feature), null, true, onPopupClose);
    }
    feature.popup = popup;
    map.addPopup(popup);
}

/*
This prints out the selected feature on the top right
*/
function showfeature(feature)
{
    document.getElementById("selected_feature").innerHTML = "<p>" + feature.name + "<br/> department <br/> location <a href=\"mailto:jsmith@mse.com\">jsmith@mse.com</a><br /></p>";
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

function overlay_getTileURL(bounds)
{
    var res = this.map.getResolution();
    var xCoor = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var yCoor = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var zoom = this.map.getZoom();
    if(xCoor >= 0 && yCoor >= 0)
    {
        return this.url + "1" + zoom + "/" + xCoor + "/" + yCoor + "." + this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

function overlay_getTileURL2(bounds)
{
    var res = this.map.getResolution();
    var xCoor = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
    var yCoor = Math.round((bounds.bottom - this.maxExtent.bottom) / (res * this.tileSize.h));
    var zoom = this.map.getZoom();
    if(xCoor >= 0 && yCoor >= 0)
    {
        return this.url + "2" + zoom + "/" + xCoor + "/" + yCoor + "." + 
        this.type;
    }
    else
    {
        return "http://www.maptiler.org/img/none.png";
    }
}

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
        var level = features[j].floor;
        var point = new OpenLayers.LonLat(features[j].long, features[j].lat);
        
        //if the deparment is checked, then we need to set its radius to 10 and give it it's picture
        
        if(level == mapLevel)
        { 
            features[j].style = OpenLayers.Util.applyDefaults({
                externalGraphic: features[j].iconName,
                pointRadius: 10
            });
            map.panTo(point);
        }
        else
        {
            if(mapLevel == 1)
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