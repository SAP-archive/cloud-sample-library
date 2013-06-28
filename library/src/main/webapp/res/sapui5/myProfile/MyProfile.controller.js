sap.ui.controller("sap.library.myProfile.MyProfile", {

	onInit : function() {

		var model = new sap.ui.model.json.JSONModel();
		this.getView().setModel(model);
		this.prepareModel();
	},

	prepareModel : function() {

		var model = this.getModel();
		model.setProperty("/currentUserInfo", getUserInfo());
		model.setProperty("/editMode", false);
		this.insertTheGenders();
	},

	getGenders : function() {
		return ["Male", "Female"];
	},

	insertTheGenders : function() {
		   var model = this.getModel();
	       var genders = this.getGenders();
	 	   model.setProperty("/genders", genders);
	},

	getModel : function() {
		return this.getView().getModel();
	},

	updateUserInfo : function() {

		startWaitingCursor();
		
		var thisController = this;
		var model = this.getModel();

		var profile = model.getProperty("/currentUserInfo");
		if (profile.gender) {
			profile.gender = getGenderAsEnum(profile.gender);
		}

		jQuery.ajax({
				type: "POST",
				url: "restricted/everyone/UpdateUserServlet",
		        contentType : "application/json",
				async: false,
				data: JSON.stringify(profile),
				success: function (data) {
					loadUserInfo();
					thisController.prepareModel();
				},
				error: function() {
					sap.ui.commons.MessageBox.alert("An error has occurred while updating user information!", "", "Please note");
					stopWaitingCursor();
		        }
			});
	},

	validateGender : function(value) {

		var errorMessage = "";

		if (!value) {
			return errorMessage;
		}

		var isValid = false;

		var genders = this.getGenders();
		for (var i=0; i<genders.length; i++) {
			if (genders[i] === value) {
				isValid = true;
			}
		}

		if (!isValid) {
			errorMessage = "Value '" + value + "' for Gender is not correct. Select value from the combo box.";
		}

		return errorMessage;
	},

	validatePhone : function(value) {

		var errorMessage = "";

		if (!value) {
			return errorMessage;
		}

		 var patt = new RegExp("^\\+[ ]?([0-9]+[ ]?)+$");

		 if (!patt.test(value)) {
	           errorMessage = "Phone must start with '+' and may contain only digits and spaces. Format: '+ XXX XX XX'";
	     }

		 return errorMessage;
	},

	enableSave : function(){

		var model = this.getModel();

		if (model.getProperty("/currentUserInfo/genderErrorMessage") || model.getProperty("/currentUserInfo/phoneErrorMessage")) {
    		model.setProperty("/disableSave", true);
    	} else {
    		model.setProperty("/disableSave", false);
    	}
	}
});
