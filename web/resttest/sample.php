<?php
session_start();
?>
<!DOCTYPE html>
<html>

<head>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, user-scalable=yes">
	<title>Sample</title>

	<link rel="stylesheet" href="./css/jquery-ui.css"/>
	<link rel="stylesheet" href="./css/main.css"/>
	<link rel="stylesheet" href="./css/form.css"/>
	<link rel="stylesheet" href="./css/breadcrumb.css"/>

	<script src="./js/jquery-1.11.1.js"></script>
	<script src="./js/jquery-ui.js"></script>
	<script src="./js/sample.js"></script>
</head>

<body>
<div id="body">
	<header>
		<a id="logo" href="./">Sample</a>
	</header>

	<div id="main">
		<div class="breadcrumb">
			<a href="./">Home</a> &gt;&gt; 
			<span class="here">Sample</span>
		</div>

		<form id="form-list" class="form">
			<table id="list">
				<thead>
					<th><input type="checkbox" name="toggle"/></th>
					<th>Name</th>
					<th>Category</th>
					<th>Deleted</th>
				</thead>
				<tbody>
				</tbody>
			</table>
			
			<button type="submit" id="add">Add</button>
			<button type="button" id="delete">Delete</button>
		</form>
		
		<div style="display:none">
			<form id="form" class="form">
				<div class="field">
					<label>Name</label>
					<input name="name" type="text" maxlength="255"/>
				</div>
				<div class="field">
					<label>Category</label>
					<input name="category" type="text" maxlength="1"/>
				</div>
				<div class="field">
					<label>Deleted</label>
					<input name="deleted" type="checkbox" value="1"/>
				</div>
			</form>
		</div>
	</div>

	<footer>
		designed by Taworn T.
	</footer>
</div>
</body>

</html>
