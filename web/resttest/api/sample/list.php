<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();

// loads data
$items = array ();
$query = "SELECT id, icon, name, category, deleted FROM sample ORDER BY id";
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