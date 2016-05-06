<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/get.php";
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
	$query = "INSERT INTO sample(id, icon, name, detail, category) VALUES(NULL, :icon, :name, :detail, :category)";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':icon' => $in['icon'],
		':name' => $in['name'],
		':detail' => $in['detail'],
		':category' => $in['category'],
	));
	$last_id = $pdo->lastInsertId();

	// reloads data
	$item = sample_get($pdo, $last_id);
}

// writes output
$out = array (
	'ok' => count($errors) <= 0,
	'errors' => $errors,
	'item' => $item,
);
echo json_encode($out);
?>