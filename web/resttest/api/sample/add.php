<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();

// gets input
$in = array (
	'icon' => isset($_POST['icon']) ? trim($_POST['icon']) : "",
	'name' => isset($_POST['name']) ? trim($_POST['name']) : "",
	'detail' => isset($_POST['detail']) ? trim($_POST['detail']) : "",
	'category' => isset($_POST['category']) ? intval($_POST['category']) : 0,
);

// checks input
$errors = array ();
if ($in['name'] == "")
	$errors[] = "Name is empty.";
if (strlen($in['name']) > 255)
	$errors[] = "Name is too long.";

if (count($errors) <= 0) {
	// adds data
	$query = "INSERT INTO sample(id, name, detail, category) VALUES(NULL, :name, :detail, :category)";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':name' => $in['name'],
		':detail' => $in['detail'],
		':category' => $in['category'],
	));
	$last_id = $pdo->lastInsertId();

	// reloads data
	$query = "SELECT id, icon, name, category, deleted FROM sample WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':id' => $last_id,
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