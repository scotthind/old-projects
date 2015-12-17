/**
 * Authors: Joseph Smith and Christopher Bowen
 * 
 * This file will provide all the functionality that a normal user requires to
 * use this software within acceptable bounds.
 */

/**
 * Prints the peripheral bubble
 * @feature The peripheral
 */
function printPeripheralBubble(feature)
{
    return (document.getElementById("selected_feature").innerHTML = "<p id=\"selected\"> Type: " + feature.Type + "</p>");
}

/*
If the feature was an employee, we need to print the employee info into a popup bubble 
*/
function printEmployeeBubble(feature)
{
    return (document.getElementById("selected_feature").innerHTML = "<p id=\"selected\"> <strong>Name:  </strong>" + 
		feature.FirstName + " " + feature.LastName + "<br/> <strong>Phone ext:  </strong>" +
        feature.Phone + "<br/><strong>Department:   </strong> "+ feature.DeptName + 
		"<hr/><a href=\'mailto:" + feature.Email + "\' target=\"_blank\"> Email </a></p>");
}

/**
 * This function displays the info of a feature when the user clicks on it
 * After we determine if it is an employee or another feature we display it
 */
function onFeatureSelect(feature)
{
    selectedFeature = feature;
    popup = new OpenLayers.Popup.FramedCloud('', feature.geometry.getBounds().getCenterLonLat(), null, (feature.attributes.type == 'personnel') ? printEmployeeBubble(selectedFeature) : printPeripheralBubble(selectedFeature), null, true, onPopupClose);
    feature.popup = popup;
    map.addPopup(popup);
}


/**
 * This unselects the feature, which in turn closes the popup
 * The parameter is the feature beign unselected
 */
function onFeatureUnselect(feature)
{
    selectedFeature = null;
    map.removePopup(feature.popup);
    document.getElementById("selected_feature").innerHTML = '[Nothing selected]';
    feature.popup.destroy();
    feature.popup = null;
}

