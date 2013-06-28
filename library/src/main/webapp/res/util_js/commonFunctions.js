var connectionErrorBox;

function loadUserInfo() {

	var core = sap.ui.getCore();

	if (!core.getModel()) {
		var model = new sap.ui.model.json.JSONModel();
		core.setModel(model);
	}
	
	var userData = requestData("restricted/everyone/ExtractUserInformationServlet");
	
	// pay special attention to the Gender -
	// since it is an enum in Java, its value is "MALE" or "FEMALE".
	// We want to display it as "Male" or "Female"
	if (userData.gender){
		userData.gender = getGenderAsString(userData.gender);
	}
	core.getModel().setData(userData);
	
}

function getUserInfo() {

	var core = sap.ui.getCore();
	if (!core.getModel()) {
		loadUserInfo();
	}

	return core.getModel().getData();
}

function formatDate(strValue) {

	if (!strValue) {
		return;
	}

	var date = new Date(strValue);

	var day = date.getDate();
	var month = date.getMonth() + 1; // months are zero-based
	var year = date.getFullYear();

	return year + "-" + month + "-" + day;
}

 function getGenderMapping () {

	   var mapping = [];

	   // Male pair
	   mapping["MALE"] = "Male";
	   mapping["Male"] = "MALE";

	   // Female pair
	   mapping["FEMALE"] = "Female";
	   mapping["Female"] = "FEMALE";

	   return mapping;
 }

 // Here as parameter can be passed either "Male" or "Female"
 function getGenderAsEnum (strGender) {
	   return this.getGenderMapping()[strGender];
 }

 // Here as parameter can be passed either "MALE" or "FEMALE"
 function getGenderAsString (enumGender) {
	   return this.getGenderMapping()[enumGender];
 }

 /**
  * This function returns a SAPUI5 cell in which the content is centered.
  * This cell can be inserted into a row in a MatrixLayout.
  * */
 function matrixCenter(element) {
	    return new sap.ui.commons.layout.MatrixLayoutCell({
	        content : element,
	        hAlign : sap.ui.commons.layout.HAlign.Center
	    });
}

 /**
  * This function returns a SAPUI5 cell in which the content is aligned to the right.
  * This cell can be inserted into a row in a MatrixLayout.
  * */
 function matrixRight(element) {
	    return new sap.ui.commons.layout.MatrixLayoutCell({
	        content : element,
	        hAlign : sap.ui.commons.layout.HAlign.Right
	    });
 }

 /**
  * This function returns a cell with a span of a specific number of rows 
  * */
 function matrixRowSpan(element, span) {
	 return new sap.ui.commons.layout.MatrixLayoutCell({
 		content : element,
		  	rowSpan : span
	  	});
 }

 /**
  * This function returns a cell with a span of a specific number of columns 
  * */
 function matrixColSpan(element, span) {
	    return new sap.ui.commons.layout.MatrixLayoutCell({
	        content : element,
	        colSpan : span
	    });
 }

 function getInputErrorImage(errorTextPathInModel) {

	 var img = new sap.ui.commons.Image({
		 src : "res/img/alert_red_16.png"
	 });

	 img.bindProperty("visible", errorTextPathInModel, function(errorText){
		 if (errorText) {
			 return true;
		 }
		 return false;
	 });

	 img.bindProperty("tooltip", errorTextPathInModel, function(errorText){
		 if (errorText){
			 return errorText;
		 }
		 return "";
	 });

	 return img;
 }

 function validateNotEmpty(value) {

 	var errorMessage = "";

 	if (!value) {
 		errorMessage = "Value cannot be empty";
 	}

 	return errorMessage;
 }

 
 /**
  * This function creates an error box which is not visible until the user tries 
  * to get data from the back-end (clicks on the tabs), but the session has expired
  * */
 function createConnectionErrorBox() {
	    connectionErrorBox = new sap.ui.commons.TextView({
	        text : "Server connection lost. After 5 seconds you will be redirected to the log in page",
	        visible : false
	    });
	    connectionErrorBox.addStyleClass("connectionError");
 }

 function startWaitingCursor() {
	 jQuery("*").addClass("waitingCursor");
 }

 function stopWaitingCursor() {
	 jQuery("*").removeClass("waitingCursor");
 }
 
 function handleExpiredSession() {
	 
	connectionErrorBox.setVisible(true);
	setTimeout(function(){
			logout("/library/index.jsp");
			}, 5000);
	
 }
 
 function logout(redirectTo) {
	 
 	jQuery.ajax({
 		method : "GET",
 		url : "restricted/everyone/LogoutServlet",
 		success : function() {
 				window.location.href = redirectTo;	
 		},
 		error : function() {
 			sap.ui.commons.MessageBox.alert("An error has occurred while trying to logout!", "", "Please note");
 		}
 	});
 }

 function requestData(url) {

	 var model = new sap.ui.model.json.JSONModel();
	 model.attachRequestFailed(function(data) {
		 handleExpiredSession();
	 });
	 
	 startWaitingCursor();
	 model.loadData(url, null, false);
	 stopWaitingCursor();
	 
	 return model.getData();
 }
