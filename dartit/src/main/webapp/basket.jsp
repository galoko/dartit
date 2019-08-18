<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/res/style.css">
		<script src="/res/jquery.js"></script>
	</head>
	<body>
		<div class="content">
			<div id="basket">
			</div>
			<input value="Buy" type="button" onclick="buy()"><br>
			<a href="/shop">Go to shop</a>
		</div>
		
		<script>
			function renderBasket() {
				var basketElement = $("#basket");
				basketElement.empty();
				
				var basket = JSON.parse(localStorage.getItem("basket"));
				if (basket) {
					Object.keys(basket.products).forEach(function (productId) {
						
						var count = basket.products[productId];
						
	 					var addProduct = function (product) {
	 						var productHtml;
							
							if (product) {
								productHtml = `<div>${product.name} for ${product.price}$ per unit. ${count} units in basket.&nbsp;`;
							} else {
								productHtml = `<div>Unknown product #${productId} for unknown price per unit. ${count} units in basket.&nbsp;`;
							}
							
							productHtml += `<input value="Remove" type="button" product_id="${productId}" onclick="removeFromBasket(event)">`;
							
							basketElement.append(productHtml);
						};
						
						$.ajax({
							  type: "GET",
							  url: "/products/" + productId,
							  dataType: "json",
							  contentType : "application/json"
							}).done(function(response) {
								addProduct(response.product);
							}).fail(function () {
								addProduct(null);
							});
					});
				}
			}
			
			function removeFromBasket(event) {
				var productId = parseInt($(event.target).attr("product_id"));
				
				var basket = JSON.parse(localStorage.getItem("basket"));
				if (basket) {
					delete basket.products[productId];
					
					localStorage.setItem("basket", JSON.stringify(basket));
					
					renderBasket();
				}
			}
			
			function buy() {
				var basket = JSON.parse(localStorage.getItem("basket"));
				if (basket) {
					$.ajax({
						  type: "POST",
						  url: "/trade",
						  data: JSON.stringify(basket),
						  dataType: "json",
						  contentType : "application/json"
						}).done(function(response) {
							localStorage.removeItem("basket");
							renderBasket();
						}).fail(function () {
							alert("Failed.");
						});
				} else {
					alert("Failed.");
				}
			}
			
			renderBasket();
		</script>
	</body>
</html>