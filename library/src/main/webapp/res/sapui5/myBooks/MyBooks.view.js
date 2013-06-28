sap.ui.jsview("sap.library.myBooks.MyBooks", {

    getControllerName : function() {
        return "sap.library.myBooks.MyBooks";
    },

    createContent : function(controller) {


    	 var myBooksTable = new sap.ui.table.Table({
   		 columns : [ {
                 label : "Title",
                 template : "lendedBook/bookName",
                 sortProperty : "lendedBook/bookName",
                 filterProperty : "lendedBook/bookName"
             }, {
                 label : "Author",
                 template : "lendedBook/authorName",
                 sortProperty : "lendedBook/authorName",
                 filterProperty : "lendedBook/authorName"
             }, {
                 label : "Remaining days",
                 template : "remainingDays",
                 sortProperty : "remainingDays",
                 filterProperty : "remainingDays"
             },  {
           	  label : "My Rating",
                 template : new sap.ui.commons.RatingIndicator({
               		maxValue: 5,
               		visualMode: sap.ui.commons.RatingIndicatorVisualMode.Continuous,
               		change: function(event) {
               			var pathToBook = event.getSource().getBindingContext().getPath() + "/lendedBook";
               			var author = controller.getModel().getProperty(pathToBook + "/authorName");
               			var title = controller.getModel().getProperty(pathToBook + "/bookName");
               			controller.updateBookRating(author, title, this.getValue());
               		}
               	}).bindProperty("value", "rating"),
    			hAlign : sap.ui.commons.layout.HAlign.Center
             }],
             selectionMode : sap.ui.table.SelectionMode.None,
             visibleRowCount : 5
         });
   	  	myBooksTable.bindRows("/");

   	  	return myBooksTable;
    }
});
