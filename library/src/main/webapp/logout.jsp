<%@ include file="header.jsp"%>

<script type="text/javascript">

var matrix = new sap.ui.commons.layout.MatrixLayout({
	columns : 1
});

var txtThankYou = new sap.ui.commons.TextView({
	text : "Thank you for using the SAP HANA Cloud Library Sample. Our team hopes you enjoyed using it as we enjoyed creating it."
});
matrix.createRow(txtThankYou);

var html = new sap.ui.core.HTML({
	content : "<p>" +
			  "If you would like to Log on onto the library again, click " +
			  "<a href =\"/library/index.jsp\">here</a>" + "." +
			  "</p>"
});
matrix.createRow(html);

var shell = new sap.ui.ux3.Shell({
	appTitle : "SAP Library Sample",
	showLogoutButton : false,
	showFeederTool : false,
	showInspectorTool : false,
	showSearchTool : false,
	content : matrix,
	worksetItems : [
					new sap.ui.ux3.NavigationItem({ text : "Logged out" })
	                ]
});

shell.placeAt("logoutContent");


</script>
</head>

<body>
<div id="logoutContent"> </div>
</body>
</html>