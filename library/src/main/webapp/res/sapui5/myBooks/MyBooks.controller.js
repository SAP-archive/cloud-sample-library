sap.ui.controller("sap.library.myBooks.MyBooks", {

    onInit : function() {
    	var model = new sap.ui.model.json.JSONModel();
		this.getView().setModel(model);
		this.loadMyBooks();
    },

    getModel : function() {
    	return this.getView().getModel();
    },

    loadMyBooks : function() {
    	var model = this.getModel();
    	model.setData(requestData("restricted/everyone/ExtractMyBooksServlet"));
    },

    updateBookRating : function(author, title, value){

    	 var thisController = this; 
    	
    	 jQuery.ajax({
 			type: "POST",
 			url: "restricted/everyone/ChangeRatingOfBookServlet?author="+author+"&title="+title+"&newRating="+value,
 			async: false,
 			success: function (data) {			
 				thisController.loadMyBooks();	
 			},
 			error: function() {
 				sap.ui.commons.MessageBox.alert("An error has occurred while updating the rating of a book!", "", "Please note");
 	        }
 		});
    }
});
