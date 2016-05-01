<?php
try {
	$pdo = new PDO("mysql:dbname=android;host=127.0.0.1:3386", "my", "mypass");
} 
catch (PDOException $e) {
	echo 'Connection failed: ' . $e->getMessage();
}
?>