<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();
if (!isset($_SESSION['login']) || $_SESSION['login'] != "55555")
	exit(json_encode(array ('errors' => "Not logged in.")));

// gets input
$path_info = explode("/", $_SERVER['PATH_INFO']);
$_PUT = array ();
parse_str(file_get_contents('php://input'), $_PUT);
$in = array (
	'id' => $path_info[1],
	'name' => isset($_PUT['name']) ? trim($_PUT['name']) : "",
	'cost' => isset($_PUT['cost']) ? doubleval($_PUT['cost']) : 0,
	'price' => isset($_PUT['price']) ? doubleval($_PUT['price']) : 0,
);

// checks input
$errors = array ();
$query = "SELECT COUNT(*) FROM product WHERE id = :id";
$stmt = $pdo->prepare($query);
$stmt->execute(array (':id' => $in['id']));
$count = $stmt->fetchColumn();
if ($count <= 0)
	$errors[] = "Id is invalid.";
if ($in['name'] == "")
	$errors[] = "Name is empty.";
if ($in['cost'] <= 0)
	$errors[] = "Cost is less or equal zero.";
if ($in['price'] <= 0)
	$errors[] = "Price is less or equal zero.";

if (count($errors) <= 0) {
	// updates data
	$query = "UPDATE product SET name = :name, cost = :cost, price = :price WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (
		':name' => $in['name'],
		':cost' => $in['cost'],
		':price' => $in['price'],
		':id' =>$in['id'],
	));

	// reloads data
	$query = "SELECT id, name, cost, price FROM product WHERE id = :id";
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