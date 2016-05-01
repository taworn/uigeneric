<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();

// gets input
$path_info = explode("/", $_SERVER['PATH_INFO']);
$_PUT = array ();
parse_str(file_get_contents('php://input'), $_PUT);
$in = array (
	'id' => $path_info[1],
	'name' => isset($_PUT['name']) ? trim($_PUT['name']) : "",
	'category' => isset($_PUT['category']) ? intval($_PUT['category']) : 0,
);

// checks input
$errors = array ();
$query = "SELECT COUNT(*) FROM sample WHERE id = :id";
$stmt = $pdo->prepare($query);
$stmt->execute(array (':id' => $in['id']));
$count = $stmt->fetchColumn();
if ($count <= 0)
	$errors[] = "Id is invalid.";
if ($in['name'] == "")
	$errors[] = "Name is empty.";

if (count($errors) <= 0) {
	// updates data
	$query = "UPDATE sample SET name = :name, category = :category WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':name' => $in['name'],
		':category' => $in['category'],
		':id' =>$in['id'],
	));

	// reloads data
	$query = "SELECT id, icon, name, category, deleted FROM sample WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':id' => $in['id'],
	));
	$row = $stmt->fetch(PDO::FETCH_ASSOC);
}

// writes output
$out = array (
	'ok' => count($errors) <= 0,
	'errors' => $errors,
	'item' => isset($row) ? $row : NULL,
);
echo json_encode($out);
?>