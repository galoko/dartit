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
			<input value="Sign in" type="button" onclick="signIn()"><br>
			<a href="registration">Go to Registration</a>
		</div>
		<script>
			function signIn() {
				var login = $("#login").val();
				var request = {
					password: $("#password").val()
				};
				
			$.ajax({
				  type: "POST",
				  url: "/users/" + login,
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