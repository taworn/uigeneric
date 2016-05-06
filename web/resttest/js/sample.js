$(function() {
	function handleError(data) {
		// use alert() for now
		console.log("data is " + data.ok);
		if (data.errors)
			alert("Error: " + data.errors[0]);
		else
			alert("Server Error");
	}

	$('[name=toggle]').change(function () {
		var checked = $(this).prop('checked');
		var list = $('#list tbody tr input[type=checkbox]');
		for (var i = 0; i < list.length; i++) {
			$(list[i]).prop('checked', checked);
		}
	});

	// adds
	$('#add').click(function() {
		var form = $('#form');
		var dialog = form.dialog({
			modal: true,
			buttons: {
				"Add New": function() {
					$.ajax({
						url: "./api/sample/add.php",
						type: 'POST',
						data: form.serialize(),
					}).done(function(data) {
						if (data.ok) {
							console.log("add data is ...");
							dialog.dialog('close');
							var tbody = $('#list tbody');
							var item = data.item;
							var tr = $(document.createElement('tr'));
							loadData(item, tr);
							tbody.append(tr);
							summary();
							console.log("add data is ok");
						}
						else {
							handleError(data);
						}
					});
				},
				Cancel: function() {
					$(this).dialog('close');
				},
			},
			open: function() {
				$(this).trigger('reset');
			},
		});
		dialog.dialog('open');
		return false;
	});

	// deletes
	$('#delete').click(function() {
		var checked = new Array();
		var elements = $('#form-list input[name=checked]');
		for (var i = 0; i < elements.length; i++) {
			var element = elements[i];
			if (element.checked)
				checked.push(element.value);
		}
		if (checked.length > 0) {
			$.ajax({
				url: "./api/sample/remove.php",
				type: 'DELETE',
				data: { list: checked }
			}).done(function(data) {
				if (data.ok) {
					console.log("delete data is ...");
					var tbody = $('#list tbody');
					tbody.empty();
					var i = 0;
					while (i < data.count) {
						var item = data.items[i];
						var tr = $(document.createElement('tr'));
						loadData(item, tr);
						tbody.append(tr);
						i++;
					}
					summary();
					console.log("delete data is ok");
				}
				else {
					handleError(data);
				}
			});
		}
	});

	// edits
	function edit(id, tr) {
		$.ajax({
			url: "./api/sample/get.php/" + id,
		}).done(function(data) {
			if (data.ok) {
				var form = $('#form');
				var dialog = form.dialog({
					modal: true,
					buttons: {
						"Save": function() {
							$.ajax({
								url: "./api/sample/edit.php/" + id,
								type: 'PUT',
								data: form.serialize(),
							}).done(function(data) {
								if (data.ok) {
									console.log("edit data is ...");
									dialog.dialog('close');
									var item = data.item;
									tr.empty();
									loadData(item, tr);
									summary();
									console.log("edit data is ok");
								}
								else {
									handleError(data);
								}
							});
						},
						Cancel: function() {
							$(this).dialog('close');
						},
					},
					open: function() {
						$(this).trigger('reset');
						$('#form input[name=name]').val(data.item.name);
						$('#form input[name=category]').val(data.item.category);
					},
				});
				dialog.dialog('open');
			}
			else {
				handleError(data);
			}
		});
	}

	// gets
	function loadData(item, tr) {
		var td;
		var click = function(e) {
			e.preventDefault();
			edit(e.data.id, tr);
		};

		td = $(document.createElement('td'));
		var checkbox = $(document.createElement('input'));
		checkbox.prop('type', 'checkbox');
		checkbox.prop('name', 'checked');
		checkbox.prop('value', item.id);
		td.append(checkbox);
		tr.append(td);

		td = $(document.createElement('td'));
		td.text(item.name);
		td.on('click', {id: item.id}, click);
		tr.append(td);

		td = $(document.createElement('td'));
		td.text(item.category);
		td.on('click', {id: item.id}, click);
		tr.append(td);

		td = $(document.createElement('td'));
		if (item.deleted == null)
			td.text(item.deleted);
		else
			td.text(item.deleted.toString());
		td.on('click', {id: item.id}, click);
		tr.append(td);

		return tr;
	}

	// lists
	function refresh() {
		$.ajax({
			url: "./api/sample/list.php",
			type: 'POST',
			dataType: 'json'
		}).done(function(data) {
			if (data.ok) {
				console.log("refresh data is ...");
				var tbody = $('#list tbody');
				var i = 0;
				while (i < data.count) {
					var item = data.items[i];
					var tr = $(document.createElement('tr'));
					loadData(item, tr);
					tbody.append(tr);
					i++;
				}
				summary();
				console.log("refresh data ok");
			}
			else {
				handleError(data);
			}
		});
	}

	function summary() {
		var list = $('#list tbody tr');
		var count = list.length;
		$('#list .summary').html(count + " record(s)");
	}

	refresh();
});
