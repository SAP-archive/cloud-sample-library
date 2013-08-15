sap.ui.jsview("sap.library.allUsers.AllUsers", {

	getControllerName : function() {
		return "sap.library.allUsers.AllUsers";
	},

	createContent : function(oController) {
		
		var allUsersTable = new sap.ui.table.Table({
   		 columns : [
   		              	  {
   		              		width : "15%",
   			 				label : "First Name",
   		 					template : "firstName",
							sortProperty : "firstName",
							filterProperty : "firstName"
						  },
						  {
							  width : "15%",
			                  label : "Last Name",
			                  template : "lastName",
			                  sortProperty : "lastName",
			                  filterProperty : "lastName"
			              },
			              {
			            	  width : "10%",
			                  label : "Email",
			                  template : "email",
			                  sortProperty : "email",
			                  filterProperty : "email"
			              }
   		            ],
   		            selectionMode : sap.ui.table.SelectionMode.None,
   		            visibleRowCount : 10,
   		            width : "70%"
		});
		allUsersTable.bindRows("/users");
		
		return allUsersTable;
	}

});
