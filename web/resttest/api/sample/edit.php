<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/get.php";
require_once "../db.php";

// checks login
session_start();

// gets input
$path_info = explode("/", $_SERVER['PATH_INFO']);
$_PUT = array ();
parse_str(file_get_contents('php://input'), $_PUT);
$in = array (
	'id' => $path_info[1],
	'icon' => isset($_PUT['icon']) ? trim($_PUT['icon']) : "",
	'name' => isset($_PUT['name']) ? trim($_PUT['name']) : "",
	'detail' => isset($_PUT['detail']) ? trim($_PUT['detail']) : "",
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
if (strlen($in['name']) > 255)
	$errors[] = "Name is too long.";

if (count($errors) <= 0) {
	// updates data
	$query = "UPDATE sample SET
		icon = :icon,
		name = :name,
		detail = :detail,
		category = :category
		WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':icon' => $in['icon'],
		':name' => $in['name'],
		':detail' => $in['detail'],
		':category' => $in['category'],
		':id' =>$in['id'],
	));

	// reloads data
	$item = sample_get($pdo, $in['id']);
}

// writes output
$out = array (
	'ok' => count($errors) <= 0,
	'errors' => $errors,
	'item' => $item,
);
echo json_encode($out);
?>