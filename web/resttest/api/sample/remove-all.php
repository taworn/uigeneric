<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/list.php";
require_once "../db.php";

// checks login
session_start();

// gets input
$_DELETE = array ();
parse_str(file_get_contents('php://input'), $_DELETE);
error_log('input: ' . file_get_contents('php://input'));
$in = array (
	'deleted' => isset($_DELETE['deleted']) ? $_DELETE['deleted'] : null,
	'category' => isset($_DELETE['category']) ? $_DELETE['category'] : null,
	'query' => isset($_DELETE['query']) ? $_DELETE['query'] : null,
);

// removes data
$query = "DELETE FROM sample";
$stmt = $pdo->prepare($query);
$stmt->execute(array ());

// loads data
$items = sample_list($pdo, $in);

// writes output
$out = array (
	'ok' => TRUE,
	'items' => isset($items) ? $items : NULL,
	'count' => isset($items) ? count($items) : 0,
);
echo json_encode($out);
?>