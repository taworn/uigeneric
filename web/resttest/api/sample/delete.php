<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../db.php";

// checks login
session_start();

// gets input
$_DELETE = array ();
parse_str(file_get_contents('php://input'), $_DELETE);
error_log('input: ' . file_get_contents('php://input'));
$in = array (
	'list' => isset($_DELETE['list']) ? $_DELETE['list'] : NULL,
);
error_log("in['list'] " . count($in['list']));;
for ($i = 0; $i < count($in['list']); $i++)
	error_log($in['list'][$i]);


// checks input
$errors = array ();
if (!is_array($in['list']))
	$errors[] = "Delete list is not parse.";

if (count($errors) <= 0) {
	// deletes data
	$query = "DELETE FROM sample WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$list = $in['list'];
	for ($i = 0; $i < count($list); $i++) {
		$stmt->execute(array (
			':id' => $list[$i],
		));
	}

	// reloads data
	$items = array ();
	$query = "SELECT id, icon, name, category, deleted FROM sample ORDER BY id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array ());
	while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
		$items[] = $row;
	}
}

// writes output
$out = array (
	'ok' => TRUE,
	'errors' => $errors,
	'items' => isset($items) ? $items : NULL,
	'count' => isset($items) ? count($items) : 0,
);
echo json_encode($out);
?>