<?php

function sample_get($pdo, $id) {
	// loads data
	$items = array ();
	$query = "SELECT id, icon, name, detail, category, UNIX_TIMESTAMP(deleted) * 1000 AS deleted
		FROM sample
		WHERE id = :id";
	$stmt = $pdo->prepare($query);
	$stmt->execute(array (':id' => $id));
	$item = $stmt->fetch(PDO::FETCH_ASSOC);

	return $item ? $item : null;
}

?>