<?php //Authors: Joseph Smith and Christopher Bowen

define("NUMFLOORS", 2);

/**
 * This function is designed to fetch specified data from a specified table
 * in a specified SQLite database and append it to a specified node
 * in a specified XML DOM document.
 * @param $name The name of the thing for which we are generating XML.
 * @param $select An array of length 1 or greater containing the names of desired attributes.
 * @param $from A string containing the name of the table from which all attributes in $select are to be fetched.
 * @param $where Discriminating criteria for selecting from $from.
 * @param $parnode The XML DOM node to which all the fetched data will be appended.
 * @param $dom The XML document containing $parnode.
 * @param $db The SQLite database to be queried.
 */
function appendAllData($name, $select, $from, $where, $parnode, $dom, $db)
{
    //Check that all parameters have been set appropriately
	if(!(isset($name, $select, $from, $parnode, $dom, $db) && is_array($select) && count($select) != 0 && is_string($from) && is_string($where)))
    {
        return;
    }
    
	//Fetch the elements we are selecting and add them to the query
    $query = "SELECT $select[0]";
    for($i = 1; $i < count($select); $i++)
    {
        $query .= ", $select[$i]";
    }

	//Add the table we are fetching from to the query
    $query .= " FROM $from";

	//If discriminating criteria have been selected, add them to the query
    if(isset($where) && $where != '')
    {
        $query .= " WHERE $where";
    }

	//Prepare and execute the constructed query
    $statement = $db->prepare($query);
    if(!$statement)
    {
        die('Query was not recognized.');
    }
    $result = $statement->execute();
    if(!$result)
    {
        $statement->close();
        $db->close();
        die('Invalid query.');
    }
    
	//Begin adding XML nodes to the document
    $newnode = $dom->createElement($name);
    $parnodeElement = $parnode->appendChild($newnode);
    // Iterate through the rows, adding XML nodes for each
    while($row = $result->fetchArray(SQLITE3_ASSOC))
    {
        $node = $dom->createElement($name . 'Row');
        $newnode = $parnodeElement->appendChild($node);
        foreach($select as $attribute)
        {
				//Strip out table names if they were included in select attributes (e.g., Icons.IconPath)
                $period_pos = stripos($attribute, '.');
                if(is_int($period_pos))
                {
                    $attribute = substr_replace($attribute, '', 0, $period_pos + 1);
                }
                $newnode->setAttribute($attribute, $row[$attribute]);
        }
    }
    $result->finalize();
    $statement->close();
}

// Start XML file, create parent node
$dom = new DOMDocument("1.0");
header("Content-type: text/xml");

$node = $dom->createElement("Everything");
$parnode = $dom->appendChild($node);

// Connect to the database 
$db = new SQLITE3('SQLiteDB/OfficeLayout.db', SQLITE3_OPEN_READONLY);

//For floor specific data, add XML nodes such that they will be named to reflect that specificity
for($floor = 1; $floor <= NUMFLOORS; $floor++)
{
//Add XML DOM branches for each table relevant to the map (e.g., not the User table)
appendAllData('EmployeeFloor' . $floor,
        array('EmployeeID', 'Cubicle.CubicleNumber', 'FirstName', 'LastName', 'Personnel.DeptName', 'Email', 'Phone', 'Latitude', 'Longitude', 'Cubicle.Floor', 'Icons.IconID', 'IconPath'),
        'Personnel natural join Cubicle natural join Department natural join Icons',
        "Floor = $floor", $parnode, $dom, $db);
appendAllData('EmergencyFloor' . $floor, array('EmergencyID', 'Icons.IconID', 'IconPath', 'Type', 'Longitude', 'Latitude', 'Floor'), 'Emergency natural join Icons', "Floor = $floor", $parnode, $dom, $db);
appendAllData('PantryFloor' . $floor, array('PantryID', 'Icons.IconID', 'Longitude', 'IconPath', 'Latitude', 'Floor'), 'Pantry natural join Icons', "Floor = $floor", $parnode, $dom, $db);
appendAllData('PeripheralsFloor' . $floor, array('PeriphID', 'Type', 'Floor', 'Icons.IconID', 'IconPath', 'Longitude', 'Latitude'), 'Peripherals natural join Icons', "Floor = $floor", $parnode, $dom, $db);
appendAllData('RoomFloor' . $floor, array('RoomID', 'Type', 'Icons.IconID', 'IconPath', 'Longitude', 'Latitude', 'Floor'), 'Room natural join Icons', "Floor = $floor", $parnode, $dom, $db);
appendAllData('UtilitiesFloor' . $floor, array('UtilID', 'Type', 'Icons.IconID', 'IconPath', 'Longitude', 'Latitude', 'Floor'), 'Utilities natural join Icons', "Floor = $floor", $parnode, $dom, $db);
}
appendAllData('Department', array('DeptName', 'Icons.IconID', 'IconPath'), 'Department natural join Icons ORDER BY DeptName', '', $parnode, $dom, $db);
appendAllData('Icons', array('IconID', 'IconPath'), 'Icons', '', $parnode, $dom, $db);

$db->close();
$dom->formatOutput = true;
echo $dom->saveXML();
?>
