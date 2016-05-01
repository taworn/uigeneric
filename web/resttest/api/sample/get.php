<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();
if (!isset($_SESSION['login']) || $_SESSION['login'] != "55555")
	exit(json_encode(array ('errors' => "Not logged in.")));

// gets input
$path_info = explode("/", $_SERVER['PATH_INFO']);
$in = array (
	'id' => $path_info[1],
);

// gets data
$query = "SELECT id, name, cost, price FROM product WHERE id = :id";
$stmt = $pdo->prepare($query);
$stmt->execute(array (':id' => $in['id']));
$row = $stmt->fetch(PDO::FETCH_ASSOC);

// writes output
$out = array (
	'ok' => TRUE,
	'item' => $row,
);
echo json_encode($out);
?>