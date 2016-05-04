<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/get.php";
require_once "../db.php";

// checks login
session_start();

// gets input
$path_info = explode("/", $_SERVER['PATH_INFO']);
$in = array (
	'id' => $path_info[1],
);

// gets data
$item = sample_get($pdo, $in['id']);

// writes output
$out = array (
	'ok' => TRUE,
	'item' => $item,
);
echo json_encode($out);
?>