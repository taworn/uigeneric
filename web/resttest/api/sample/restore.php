<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "./lib/list.php";
require_once "../db.php";

// checks login
session_start();

// gets input
$_POST = array ();
parse_str(file_get_contents('php://input'), $_POST);
error_log('input: ' . file_get_contents('php://input'));
$in = array (
	'list' => isset($_POST['list']) ? $_POST['list'] : NULL,
	'deleted' => isset($_POST['deleted']) ? $_POST['deleted'] : null,
	'category' => isset($_POST['category']) ? $_POST['category'] : null,
	'query' => isset($_POST['query']) ? $_POST['query'] : null,
);
error_log("in['list'] " . count($in['list']));
for ($i = 0; $i < count($in['list']); $i++)
	error_log($in['list'][$i]);

// checks input
$errors = array ();
if (!is_array($in['list']))
	$errors[] = "Restore list is not parsed.";

if (count($errors) <= 0) {
	// moves data out of trash
	$query = "UPDATE sample SET deleted = NULL WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$list = $in['list'];
	for ($i = 0; $i < count($list); $i++) {
		$stmt->execute(array (
			':id' => $list[$i],
		));
	}

	// loads data
	$items = sample_list($pdo, $in);
}

// writes output
$out = array (
	'ok' => count($errors) <= 0,
	'errors' => $errors,
	'items' => isset($items) ? $items : NULL,
	'count' => isset($items) ? count($items) : 0,
);
echo json_encode($out);
?>