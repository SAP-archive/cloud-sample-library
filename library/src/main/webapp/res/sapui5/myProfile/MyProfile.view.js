sap.ui.jsview("sap.library.myProfile.MyProfile", {

    getControllerName : function() {
        return "sap.library.myProfile.MyProfile";
    },

    createContent : function(controller) {

    	var panel = new sap.ui.commons.Panel({
    		title : new sap.ui.commons.Title({
    			text : "Personal Data"
    		}),
    		showCollapseIcon : false,
    		width : "70%"
    	});


    	// the matrix is the content that will be inserted into the panel
    	var matrix = new sap.ui.commons.layout.MatrixLayout({
    		columns : 4,
   		  	widths : ["30%", "25%", "35%", "10%"]
   	     });

    	// Insert the profile image
    	var userImage = new sap.ui.commons.Image({
    		src : "{/currentUserInfo/imgSrc}"
    	});
    	userImage.addStyleClass("profileImage");

    	var cellImage = matrixRowSpan(userImage, 7);

    	// Insert first name
    	var lblFirstName = new sap.ui.commons.Label({
    		text : "First Name"
    	});
    	var fldFirstName = new sap.ui.commons.TextField({
   		  	editable : false,
   		  	width : "100%",
   		  	value : "{/currentUserInfo/firstName}"
   	  	});
    	matrix.createRow(cellImage, lblFirstName, fldFirstName);

    	// insert last name
    	var lblLasttName = new sap.ui.commons.Label({text:"Last Name"});
   	  	var fldLastName = new sap.ui.commons.TextField({
   	  		editable : false,
   	  		width : "100%",
   	  		value : "{/currentUserInfo/lastName}"
   	  	});
   	  	matrix.createRow(lblLasttName, fldLastName);

	   	// Insert email
	   	var lblEmail = new sap.ui.commons.Label({text:"Email"});
	   	var fldEmail = new sap.ui.commons.TextField({
	   		editable : false,
	   		width : "100%",
	   		value : "{/currentUserInfo/email}"
	   	});
	   	matrix.createRow(lblEmail, fldEmail);

	   	// Insert gender
	   	var lblGender = new sap.ui.commons.Label({text : "Gender"});
	   	var cmbGender = new sap.ui.commons.ComboBox({
	   		editable : "{/editMode}",
	   		width : "100%",
	   		value: "{/currentUserInfo/gender}",
	   		change : function(event) {
	   			var value = this.getValue();
	   			var errorMessage = controller.validateGender(value);
	   			controller.getModel().setProperty("/currentUserInfo/genderErrorMessage", errorMessage);
	   			controller.enableSave();
	   		}
	   	});
	   	var genderTemplate = new sap.ui.core.ListItem();
	   	genderTemplate.bindProperty("text", "");
	   	cmbGender.bindAggregation("items", "/genders", genderTemplate);
	   	matrix.createRow(lblGender, cmbGender, matrixCenter(getInputErrorImage("/currentUserInfo/genderErrorMessage")));

	   	// Insert phone
	   	var lblPhone = new sap.ui.commons.Label({
	   		text : "Phone"
	   	});
	   	var fldPhone = new sap.ui.commons.TextField({
	   		width : "100%",
	   		editable : "{/editMode}",
	   		value : "{/currentUserInfo/phone}",
	   		change : function(event) {
	   			var value = jQuery.trim(this.getValue());
	   			var errorMessage = controller.validatePhone(value);
	   			controller.getModel().setProperty("/currentUserInfo/phoneErrorMessage", errorMessage);
	   			controller.enableSave();
	   		}
	   	});
	   	matrix.createRow(lblPhone, fldPhone, matrixCenter(getInputErrorImage("/currentUserInfo/phoneErrorMessage")));

	   	// Insert address
	   	var lblAddress = new sap.ui.commons.Label({text : "Address"});
	   	var areaAddress = new sap.ui.commons.TextArea({
	   		editable : "{/editMode}",
	   		width : "100%",
	   		value : "{/currentUserInfo/address}"
	   	});
	   	matrix.createRow(lblAddress, areaAddress);

	   	// Insert the buttons
	   	var cellButtons = new sap.ui.commons.layout.MatrixLayoutCell({
   		  	colSpan : 2
   	  	});

	    var btnEdit = new sap.ui.commons.Button({
	    	text : "Edit",
  		  	press : function(event){
  		  		controller.getModel().setProperty("/editMode", true);
  		  	}
  	  	});
	    btnEdit.bindProperty("visible", "/editMode", function(editMode){

	    	var shouldEditButtonBeVisible;

	    	if (editMode){
	    		shouldEditButtonBeVisible = false;
  		  	}else{
  		  		shouldEditButtonBeVisible = true;
  		  	}

	    	return shouldEditButtonBeVisible;
	    });	
	    cellButtons.addContent(btnEdit);

	    var btnSave = new sap.ui.commons.Button({
  		  	text : "Save",
  		  	visible : "{/editMode}",
  		  	press : function(event){
  		  		controller.updateUserInfo();
  		  	}
	    });
	    btnSave.bindProperty("enabled", "/disableSave", function(bValue){
    		if (bValue === true) {
    			return false;
    		}
    		return true;
    	});
	    cellButtons.addContent(btnSave);

	    matrix.createRow(cellButtons);


	    // Insert the uploading of a picture functionality
	    lblNewImage = new sap.ui.commons.Label({
	    	text : "Upload a new profile image"
	    });

	    matrix.createRow(lblNewImage);

	    var fileUploader = new sap.ui.commons.FileUploader("imageUploader", {
    		uploadUrl : "restricted/everyone/ImageUploadServlet",
    		name : "imageUploader",
    		uploadOnChange : false,
    		change : function(event) {
    			
    			var editMode = controller.getModel().getData().editMode;
    			
    			if (editMode === true) {
    				this.setValue("");
    				sap.ui.commons.MessageBox.alert("You cannot upload an image untill the changes to your profile are saved." + 
    						"\n\n" + "Click on save before you may continue.","", "Please note");
    			} else {
    				this.upload();
    			}
    		},
    		uploadComplete : function(){

    			this.setValue("");
    			loadUserInfo();
    			controller.prepareModel();
    			var imgSrc = controller.getModel().getProperty("/currentUserInfo/imgSrc");
    			if (imgSrc.indexOf("default_user_image.jpg") === -1) {
	    			imgSrc = imgSrc + "&refresh=" + Math.random();
	    			controller.getModel().setProperty("/currentUserInfo/imgSrc", imgSrc);
    			}
			}
    	}); 	  	
  	  	
	    matrix.createRow(fileUploader);

   	  	panel.addContent(matrix);

    	return panel;

    }
});
