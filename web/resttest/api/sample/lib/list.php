<?php

function sample_list($pdo, $in) {
	// loads data
	$items = array ();
	$query = "SELECT id, icon, name, category, UNIX_TIMESTAMP(deleted) * 1000 AS deleted
		FROM sample
		WHERE 0 = 0";
	$params = array ();
	if (isset($in['deleted'])) {
		if (!$in['deleted'])
			$query .= " AND deleted IS NULL";
		else
			$query .= " AND deleted IS NOT NULL";
	}
	if (isset($in['category'])) {
		$query .= " AND category = " . $in['category'];
	}
	if (isset($in['query'])) {
		$search = "'%" . str_replace("'", "''", $in['query']) . "%'";
		$query .= " AND name LIKE " . $search;
	}

	// executes query
	$stmt = $pdo->prepare($query);
	$stmt->execute(array ());
	while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
		$items[] = $row;
	}

	return $items;
}

?>