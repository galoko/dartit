<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/res/style.css">
		<script src="/res/jquery.js"></script>
	</head>
	<body>
		<div class="content">
			Login:<br>
			<input type="text" id="login" size="32" /><br>
			Password:<br>
			<input type="password" id="password" size="128" /><br>
			Manager
			<input type="checkbox" id="isManager"><br>
			<input value="Register" type="button" onclick="register()"><br>
			<a href="/signIn">Go to Sign in</a>
		</div>
		<script>
			function register() {
				var request = {
					login: $("#login").val(),
					password: $("#password").val(),
					isManager: !!$("#isManager").prop("checked")
				};
				
			$.ajax({
				  type: "POST",
				  url: "/users",
				  data: JSON.stringify(request),
				  dataType: "json",
				  contentType : "application/json"
				}).done(function(response) {
					location.href = "/";
				}).fail(function () {
					alert("Failed.");
				});
			}
		</script>
	</body>
</html>