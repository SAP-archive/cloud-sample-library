sap.ui.controller("sap.library.allBooks.AllBooks", {

    onInit : function() {
    	var model = new sap.ui.model.json.JSONModel();
    	this.getView().setModel(model);
    	model.setProperty("/roles", getUserInfo().roles);
    	model.setProperty("/books", []);
    	model.setProperty("/details", {});
    	this.loadBooks();
    },

    getModel : function() {
    	return this.getView().getModel();
    },

    loadBooks : function() {

    	var model = this.getModel();
    	model.setProperty("/books", requestData("restricted/everyone/ExtractAllBooksServlet"));
  },

    saveBook : function(book){
    	
    	startWaitingCursor();
    	
    	var url = "restricted/admin";
    	
    	if (!book.previousBookName && !book.previousAuthorName) {
    		url = url + "/SaveBookServlet" + "?bookName=" + book.bookName + "&authorName=" + book.authorName;
    	} else {
    		url = url + "/EditBookServlet" + "?bookName=" + book.previousBookName + "&authorName=" + book.previousAuthorName;
    	}

    	var thisController = this;

    	jQuery.ajax({
			type: "POST",
			url: url,
	        contentType : "application/json",
			data: JSON.stringify(book),
			success: function () {
				thisController.loadBooks();
			},
			error: function(jqXHR) {
				
				if (jqXHR.status === 400) {
					
					var response = JSON.parse(jqXHR.responseText);
					
					if (response.canProceed === false) {
						thisController.showAlertOperationCannotBeExecuted("reserved");
						return;
					}
					
					if (response.alreadyExists) {
						sap.ui.commons.MessageBox.alert("A book with the same name and author already exists!", "", "Please note");
						stopWaitingCursor();
					}
								
				} else {
					sap.ui.commons.MessageBox.alert("An error has occurred while saving book!", "", "Please note");
					stopWaitingCursor();
				}
				
	        }
		});
    },
    
    removeBook : function(book) {

    	startWaitingCursor();
    	
    	var thisController = this;

    	jQuery.ajax({
			type: "DELETE",
			url: "restricted/admin/RemoveBookServlet?bookName=" + book.bookName + "&authorName=" + book.authorName,
			success: function () {
				thisController.loadBooks();
			},
			error: function(jqXHR) {
				
				if (jqXHR.status === 400) {
					var response = JSON.parse(jqXHR.responseText);
					
					if (response.canProceed === false) {
						thisController.showAlertOperationCannotBeExecuted("reserved");
						return;
					}
					
				} else if (jqXHR.status === 405) {
					// session expired
					handleExpiredSession();
					return;
				} else {
					sap.ui.commons.MessageBox.alert("An error has occurred while deleting book!", "", "Please note");
					stopWaitingCursor();
				}
	        }
		});
    },
    
    reserveBook : function(book) {

    	startWaitingCursor();
    	
    	var thisController = this;

    	jQuery.ajax({
			type: "POST",
			url: "restricted/everyone/ReserveBookServlet?bookName=" + book.bookName + "&authorName=" + book.authorName,
	        contentType : "application/json",
			success: function (data) {
				thisController.loadBooks();
				thisController.sendMail("reserved", book);		
			},
			error: function(jqXHR) {
				
				if (jqXHR.status === 400) {
					var response = JSON.parse(jqXHR.responseText);
					
					if (response.canProceed === false) {
						thisController.showAlertOperationCannotBeExecuted("reserved");
						return;
					}
					
				} else {
					sap.ui.commons.MessageBox.alert("An error has occurred while reserving book!", "", "Please note");
					stopWaitingCursor();
				}
	        }
		});
    },
    
    /**
     * Request sending an email.
     * 
     * @param status - if the mail is going to be sent for reserving a book or for returning a book.
     * 	allowed values are: "reserved", "returned"
     * @book - book related to the user's action
     * */
    sendMail : function(status, book){
    
    	if (status !== "reserved" && status !== "returned") {
    		sap.ui.commons.MessageBox.alert("Cannot send mail due to invalid value for parameter status!", "", "Please note");
    		return;
    	}
    	
    	jQuery.ajax({
			type: "POST",
			url: "restricted/everyone/SendMailServlet?status=" + status + "&bookName=" + book.bookName + "&authorName=" + book.authorName,
	        contentType : "application/json",
			success: function () {
				// do nothing, the user will receive the email
			},
			error: function() {
				sap.ui.commons.MessageBox.alert("Could not send email!", "", "Please note");
	        }
		});
    	
    },

    getBookDetails : function(isbn){    	
    	var model = this.getModel();
    	
    	startWaitingCursor();
    	
    	jQuery.ajax({
    		type : "GET",
    		url : "restricted/everyone/ExtractBookDetailsServlet?isbn=" + isbn,
    		success : function(data) {
    			
    			model.setProperty("/details/data", data);
    			stopWaitingCursor();
    		},
    		error : function(jqXHR) {
    			if (jqXHR.status === 400) {
    				sap.ui.commons.MessageBox.alert("Details could not be extracted." + "\n" + 
    						"Check if the OpenLibrary destination is configured properly.", "", "Please note");
    				stopWaitingCursor();
    			} else {
    				sap.ui.commons.MessageBox.alert("An error has occurred while extracting book details!", "", "Please note");
    				stopWaitingCursor();
    			}
    		}
    	});
    },

    confirmBookReturning : function(book) {

    	startWaitingCursor();
    	
		var thisController = this;

		jQuery.ajax({
			type: "POST",
			async : false,
			url: "restricted/admin/ReturnBookServlet?returningBook&bookName=" + book.bookName + "&authorName=" + book.authorName + "&userId=" + book.reservedByUserId,
	        contentType : "application/json",
			success: function (data) {
				thisController.loadBooks();
				thisController.sendMail("returned", book);
			},
			error: function(jqXHR) {
				
				if (jqXHR.status === 400) {
					var response = JSON.parse(jqXHR.responseText);
					
					if (response.canProceed === false) {
						thisController.showAlertOperationCannotBeExecuted("returned");
						return;
					}
					
				} else {
					sap.ui.commons.MessageBox.alert("An error has occurred while returning book to library!", "", "Please note");
					stopWaitingCursor();
				}
	        }
		});
	},
	
    validateIsbn : function(isbnValue) {

    	var errorMessage = "";

    	if (!isbnValue){
    		return errorMessage;
    	}

        var patt = new RegExp("^[0-9]+$");

        if (!patt.test(isbnValue)) {
           errorMessage = "ISBN should contain only digits";
        }

        return errorMessage;
    },

    enableSaveButton : function(model) {

    	if (model.getProperty("/isbnErrorMessage") || model.getProperty("/titleErrorMessage") || model.getProperty("/authorErrorMessage")) {
    		model.setProperty("/disableSave", true);
    	} else {
    		model.setProperty("/disableSave", false);
    	}
    },
    
    /**
     * Here as parameter only values "reserved" and "returned" are allowed
     * */
    showAlertOperationCannotBeExecuted : function(bookState) {
    	
    	if (bookState !== "reserved" && bookState !== "returned") {
    		sap.ui.commons.MessageBox.alert("Cannot display message box due to invalid value passed to function!", "", "Please note");
    		return;
    	}
    	
    	var thisController = this;
    	
    	sap.ui.commons.MessageBox.alert("Cannot complete the operation, because book is already " + bookState + "." + "\n\n" + "After you close this dialog content will be refreshed.", function(){
    		thisController.loadBooks();
    	}, "Please note");
    },
    
    isUserAdmin : function (arrRoles) {
    	
    	var isAdmin = false;
    	
    	for (var i=0; i<arrRoles.length; i++) {
    		if (arrRoles[i] === "admin") {
    			isAdmin = true;
    		}
    	}
    	
    	return isAdmin;
    }

});
