<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();

// gets input
$in = array (
	'deleted' => isset($_POST['deleted']) ? $_POST['deleted'] : null,
	'category' => isset($_POST['category']) ? $_POST['category'] : null,
	'search' => isset($_POST['search']) ? $_POST['search'] : null,
);

// loads data
$items = array ();
$query = "SELECT id, icon, name, category, deleted
	FROM sample
	WHERE 0 = 0";
$params = array ();
if (isset($in['deleted'])) {
	if (!$in['deleted'])
		$query .= " AND deleted IS NULL";
	else
		$query .= " AND deleted IS NOT NULL";
}
if (isset($in['category'])) {
	$query .= " AND category = " . $in['category'];
}
if (isset($in['search'])) {
	$search = "'%" . $in['search'] . "%'";
	$query .= " AND name LIKE " . $search;
}

$stmt = $pdo->prepare($query);
$stmt->execute(array ());
while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
	$items[] = $row;
}

// writes output
$out = array (
	'ok' => TRUE,
	'items' => $items,
	'count' => count($items),
);
echo json_encode($out);
?>