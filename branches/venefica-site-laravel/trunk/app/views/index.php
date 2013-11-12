<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Gifteng | Give. Receive. Inspire.</title>
		
		<link rel="stylesheet/less" type="text/css" href="assets/less/app.less" />
		<link href="assets/css/gifteng.css" type="text/css" rel="stylesheet" />
		<link href='http://fonts.googleapis.com/css?family=Lato:300,400,700' rel='stylesheet' type='text/css'>

		<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.css" />
	    <!--[if lte IE 8]>
	    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.ie.css" />
	    <![endif]-->
		
		<script src="assets/js/less.min.js" type="text/javascript"></script>
		
		<script data-main="app/requirements.js" src="app/lib/require.js"></script>
		<script> /* Provisory for dev environment: */ localStorage.clear(); </script>
	</head>
	<body>
  		<fb app-id='285994388213208'></fb>
		<div ng-include="'app/partials/navbar.html'" ng-controller="NavController"></div>
		<div class="snap-content snap-slide">
			<div class="container ge-container">
				<div class="ng-view"></div>
			</div>
		</div>
	</body>
</html>