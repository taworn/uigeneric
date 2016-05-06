<?php
try {
	$pdo = new PDO("mysql:dbname=android;host=127.0.0.1", "root", "");
}
catch (PDOException $e) {
	echo 'Connection failed: ' . $e->getMessage();
}
?>