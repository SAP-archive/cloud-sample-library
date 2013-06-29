<%@ include file="header.jsp"%>
<title>SAP Library</title>


<script type="text/javascript"
		src="/library/res/util_js/commonFunctions.js"></script>

<script type="text/javascript">
	createConnectionErrorBox();
</script>


<link href="/library/res/styles/lib_styles.css" rel="stylesheet"
	type="text/css">


</head>

<script>

loadUserInfo();

var view = sap.ui.view({
	viewName : "sap.library.mainView.Main",
	type : sap.ui.core.mvc.ViewType.JS
});
view.placeAt("content");

connectionErrorBox.placeAt("content");

</script>

<body class="sapUiBody">
	<div id="content"></div>
</body>
</html>
