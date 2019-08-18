<!DOCTYPE html>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="/res/style.css">
		<script src="/res/jquery.js"></script>
	</head>
	<body>
		<div class="content">
			<h2>This is <b>DartIT</b> online store!</h2>
			<div id="products">
			</div>
			<a href="/basket">Go to basket.</a>
		</div>
		<script>
			$.ajax({
				  type: "GET",
				  url: "/products/",
				  dataType: "json",
				  contentType : "application/json"
				}).done(function(response) {
					var productsElement = $("#products");
					
					response.products.forEach(function (product) {
						var productHtml = 
							`<div>${product.name} for ${product.price}$ per unit. ${product.amount} units is available.&nbsp;` +
							`<input type='number' id="product_input_${product.id}" size="3" min="1"><input value="Add to basket" type="button" product_id="${product.id}" onclick="addToBasket(event)">`;
							
						productsElement.append(productHtml);
					});
				}).fail(function () {
					console.log("Failed to get products.");
				});
			
			function addToBasket(event) {
				var productId = parseInt($(event.target).attr("product_id"));
				var count = parseInt($("#product_input_" + productId).val());
				if (!(count > 0)) {
					return;
				}
				
				var basket = JSON.parse(localStorage.getItem("basket"));
				if (!basket) {
					basket = {
						products: {}
					};
				}
				
				if (!(productId in basket.products)) {
					basket.products[productId] = 0;
				}
				
				basket.products[productId] += count;
				
				localStorage.setItem("basket", JSON.stringify(basket));
			}
		</script>
	</body>
</html>