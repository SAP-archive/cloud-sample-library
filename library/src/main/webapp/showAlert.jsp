<%@ include file="header.jsp"%>

<script type="text/javascript"
		src="/library/res/util_js/commonFunctions.js"></script>

<script type="text/javascript">
	createConnectionErrorBox();
</script>


<link href="/library/res/styles/lib_styles.css" rel="stylesheet"
	type="text/css">

<script type="text/javascript">
    var message = "";

    message = <%="\""
					+ request.getSession().getAttribute("message").toString()
					+ "\""%>;


	alert(message);
</script>
</head>
</html>