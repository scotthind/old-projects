//Authors: Joseph Smith and Christopher Bowen

<?php
// Start XML file, create parent node
$dom = new DOMDocument("1.0");
$node = $dom->createElement("everything");
$parnode = $dom->appendChild($node);
$newnode = $dom->createElement("cubicles");
$parnodeCubicles = $parnode->appendChild($newnode);

// Set the active SQLite database
$db = sqlite_open('SQLiteDB/OfficeLayout.db', 0666, $sqliteerror);
if(!$db)
{
    die("Can\'t use db : " . $sqliteerror);
}

// Search the rows in the markers table
$query = "SELECT cubeID, floor, cubeNumber, cubicle.personID, loclat, loclong, email, phone, Department.deptName, firstName, lastName, iconPath FROM cubicle natural join Personnel natural join Department natural join Icons";	
$result = sqlite_query($db, $query);
if(!$result)
{
    die("Invalid query: " . sqlite_error());
}

header("Content-type: text/xml");

// Iterate through the rows, adding XML nodes for each
while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
{
    $node = $dom->createElement("cubicle");
    $newnode = $parnodeCubicles->appendChild($node);
    $newnode->setAttribute("cubeID", $row['cubeID']);
    $newnode->setAttribute("floor", $row['floor']);
    $newnode->setAttribute("cubeNumber", $row['cubeNumber']);
    $newnode->setAttribute("personID", $row['cubicle.personID']);
    $newnode->setAttribute("lat", $row['loclat']);
    $newnode->setAttribute("long", $row['loclong']);
    $newnode->setAttribute("email", $row['email']);
    $newnode->setAttribute("phone", $row['phone']);
    $newnode->setAttribute("deptName", $row['Department.deptName']);
    $newnode->setAttribute("firstName", $row['firstName']);
    $newnode->setAttribute("lastName", $row['lastName']);
    $newnode->setAttribute("iconPath", $row['iconPath']);	
}

//continue to print out the xml for all the different tables and a bunch of different queries so we can easily 
//manipulate the data in the javascript files

$query = "SELECT personID, email, phone, deptName FROM Personnel";
$result = sqlite_query($db, $query);
if(!$result) 
{
    die("Invalid query: " . sqlite_error());
}

$node = $dom->createElement("employees");
$parnodeEmployees = $parnode->appendChild($node);

// Iterate through the rows, adding XML nodes for each
while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
{
    $node = $dom->createElement("employee");
    $newnode = $parnodeEmployees->appendChild($node);
    $newnode->setAttribute("personID", $row['personID']);
    $newnode->setAttribute("email", $row['email']);
    $newnode->setAttribute("phone", $row['phone']);
    $newnode->setAttribute("deptName", $row['deptName']);	
}

$query = "SELECT iconID FROM Icons";
$result = sqlite_query($db, $query);
if(!$result)
{
    die("Invalid query: " . sqlite_error());
}

$node = $dom->createElement("icons");
$parnodeEmployees = $parnode->appendChild($node);

// Iterate through the rows, adding XML nodes for each
while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
{
    $node = $dom->createElement("icon");
    $newnode = $parnodeEmployees->appendChild($node);
    $newnode->setAttribute("iconID", $row['iconID']);	
}

$query = "SELECT type, floor, iconPath, Icons.iconID, long, lat, utilID FROM Utilities natural join Icons";
$result = sqlite_query($db, $query);
if(!$result)
{
    die("Invalid query: " . sqlite_error());
}

$node = $dom->createElement("utilities");
$parnodeUtil = $parnode->appendChild($node);

// Iterate through the rows, adding XML nodes for each
while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
{
    $node = $dom->createElement("utility");
    $newnode = $parnodeUtil->appendChild($node);
    $newnode->setAttribute("type", $row['type']);
    $newnode->setAttribute("floor", $row['floor']);
    $newnode->setAttribute("iconPath", $row['iconPath']);
    $newnode->setAttribute("long", $row['long']);
    $newnode->setAttribute("lat", $row['lat']);
    $newnode->setAttribute("utilID", $row['utilID']);
    $newnode->setAttribute("iconID", $row['Icons.iconID']);	
}

$query = "SELECT deptName, Department.iconID, iconPath  FROM Department natural join Icons";
$result = sqlite_query($db, $query);
if(!$result)
{
    die("Invalid query: " . sqlite_error());
}

$node = $dom->createElement("Departments");
$parnodeDepartments = $parnode->appendChild($node);

// Iterate through the rows, adding XML nodes for each
while($row = @sqlite_fetch_array($result, SQLITE_ASSOC))
{
    $node = $dom->createElement("department");
    $newnode = $parnodeDepartments->appendChild($node);
    $newnode->setAttribute("deptName", $row['deptName']);
    $newnode->setAttribute("iconPath", $row['iconPath']);	
}

sqlite_close($db);
$dom->formatOutput = true;
echo $dom->saveXML();
?>