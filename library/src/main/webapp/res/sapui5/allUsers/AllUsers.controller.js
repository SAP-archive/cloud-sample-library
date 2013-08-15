sap.ui.controller("sap.library.allUsers.AllUsers", {

	onInit: function() {
		var model = new sap.ui.model.json.JSONModel();
		this.getView().setModel(model);
		model.setProperty("/users", []);
    	
    	this.loadUsers();
	},
	
	getModel : function() {
	    	return this.getView().getModel();
	},

	loadUsers : function() {

	    var model = this.getModel();
	    if(isUserAdmin(getUserInfo().roles)){
            model.setProperty("/users", requestData("restricted/admin/ExtractAllUsersServlet"));
	    }
	}
});