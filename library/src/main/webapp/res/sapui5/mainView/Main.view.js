sap.ui.jsview("sap.library.mainView.Main", {

    getControllerName : function() {
        return "sap.library.mainView.Main";
    },

    createContent : function(controller) {

  	  var allBooksView = sap.ui.view({
  		  viewName : "sap.library.allBooks.AllBooks",
  		  type : sap.ui.core.mvc.ViewType.JS
  	  });

  	  var myBooksView = sap.ui.view({
  		  viewName : "sap.library.myBooks.MyBooks",
  		  type : sap.ui.core.mvc.ViewType.JS
  	  });

  	  var profileView = sap.ui.view({
  		  viewName : "sap.library.myProfile.MyProfile",
  		  type : sap.ui.core.mvc.ViewType.JS
  	  });
  	  
  	  var allUsersView = sap.ui.view({
		  viewName : "sap.library.allUsers.AllUsers",
		  type : sap.ui.core.mvc.ViewType.JS
	  });
  	  
  	  var itemAllUsers =  new sap.ui.ux3.NavigationItem("WI_allUsers", {key : "wi_allUsers",text:"All Users"});
  	  setVisibleIfAdmin(itemAllUsers);

  	  var shell = new sap.ui.ux3.Shell({
  	        appTitle : "SAP Library Sample",
  	        appIcon : "/library/res/img/SAPLogo.gif",
  	        appIconTooltip : "SAP logo",
  	        content : allBooksView,
  	        showLogoutButton : true,
  	        showSearchTool : false,
  	        showInspectorTool : false,
  	        showFeederTool : false,
  	        worksetItems : [
  	                       new sap.ui.ux3.NavigationItem("WI_allBooks", {key : "wi_allBooks", text : "Library"}),
  	                       new sap.ui.ux3.NavigationItem("WI_myBooks", {key : "wi_myBooks", text : "My Books"}),
  	                       new sap.ui.ux3.NavigationItem("WI_myProfile", {key : "wi_myProfile",text:"My Profile"}),
  	                       itemAllUsers
  	                       ],
  	        headerItems : [
  	                      new sap.ui.commons.TextView({
  	                    	  text : "{/displayName}",
  	                    	  tooltip : "User Name"
  	                      })
  	            ],
  	        worksetItemSelected : function(event){
  	                var sId = event.getParameter("id");
  	                var shell = event.getSource();
  	                switch (sId) {
  	                case "WI_allBooks":
  	                        shell.setContent(allBooksView);
  	                        allBooksView.getController().loadBooks();
  	                        break;
  	                case "WI_myBooks":
  	                		shell.setContent(myBooksView);
  	                        myBooksView.getController().loadMyBooks();
  	                        break;
  	                case "WI_myProfile":
  	                		shell.setContent(profileView);
  	                        break;
  	                case "WI_allUsers":
	                		shell.setContent(allUsersView);
	                		allUsersView.getController().loadUsers();
	                        break;
  	                default:
  	                        break;
  	                }
  	        },
  	        logout : function(){
  	                logout("/library/logout.jsp");
  	        }
  	  });


  	  return shell;
    }

});
