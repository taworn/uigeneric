<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/list.php";
require_once "../db.php";

// checks login
session_start();

// gets input
$in = array (
	'deleted' => isset($_POST['deleted']) ? $_POST['deleted'] : null,
	'category' => isset($_POST['category']) ? $_POST['category'] : null,
	'query' => isset($_POST['query']) ? $_POST['query'] : null,
);

// loads data
$items = sample_list($pdo, $in);

// writes output
$out = array (
	'ok' => TRUE,
	'items' => $items,
	'count' => count($items),
);
echo json_encode($out);
?>