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
	'list' => isset($_DELETE['list']) ? $_DELETE['list'] : NULL,
	'deleted' => isset($_DELETE['deleted']) ? $_DELETE['deleted'] : null,
	'category' => isset($_DELETE['category']) ? $_DELETE['category'] : null,
	'query' => isset($_DELETE['query']) ? $_DELETE['query'] : null,
);
error_log("in['list']: " . count($in['list']));
for ($i = 0; $i < count($in['list']); $i++)
	error_log($in['list'][$i]);

// checks input
$errors = array ();
if (!is_array($in['list']))
	$errors[] = "Remove list is not parsed.";

if (count($errors) <= 0) {
	// removes data
	$query = "DELETE FROM sample WHERE id = :id";
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