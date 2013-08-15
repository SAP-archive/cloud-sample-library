sap.ui.jsview("sap.library.allBooks.AllBooks", {

    getControllerName : function() {
        return "sap.library.allBooks.AllBooks";
    },

    createContent : function(controller) {

    	var thisView = this;

    	// This matrix holds three rows - one for the Add button,
    	// one for the table with all the books and one for the book preview
    	var matrix = new sap.ui.commons.layout.MatrixLayout({
    		width : "100%",
    		columns : 1
    	});

    	var btnAdd = new sap.ui.commons.Button({
    		text : "Add New Books",
    		press : function(event) {
    			thisView.openDialogBook(controller);
    		}
    	});
    	setVisibleIfAdmin(btnAdd);
    	
    	var lnkTitleTemplate = new sap.ui.commons.Link({
    		 text : "{bookName}",
    		 press : function(event) {
    			 var pathToBook = event.getSource().getBindingContext().getPath();
    			 var isbn = controller.getModel().getProperty(pathToBook + "/isbn");
    			 controller.getBookDetails(isbn);
    		 }
    	});
    	this.bindPropertyAccordingToISBN(lnkTitleTemplate, "enabled", true, false);

    	var btnReserveTemplate = new sap.ui.commons.Button({
    		text: "Reserve",
			press: function(event) {
				this.setEnabled(false);
				var pathToBook = event.getSource().getBindingContext().getPath();
    			var book = controller.getModel().getProperty(pathToBook);
    			controller.reserveBook(book);
			}
		});
    	this.bindPropertyIfReserved(btnReserveTemplate, "enabled", false, true);
    	btnReserveTemplate.addStyleClass("bigSpaceToRight");
    	
    	var btnEditTemplate = new sap.ui.commons.Button({
    		text : "Edit",
    		press : function(event) {
    			var pathToBook = event.getSource().getBindingContext().getPath();
    			var book = controller.getModel().getProperty(pathToBook);
    			var strBook = new String(JSON.stringify(book));
    			var copy = JSON.parse(strBook);
    			thisView.openDialogBook(controller, copy);
    		}
    	});
    	this.bindPropertyIfReserved(btnEditTemplate, "enabled", false, true);
    	btnEditTemplate.addStyleClass("smallSpaceToRight");
    	setVisibleIfAdmin(btnEditTemplate);
    	
    	var btnRemoveTemplate = new sap.ui.commons.Button({
    		text : "Remove",
    		press : function(event) {
    			var pathToBook = event.getSource().getBindingContext().getPath();
    			var book = controller.getModel().getProperty(pathToBook);

                sap.ui.commons.MessageBox.confirm("Do you want to remove book " + book.bookName + "?", function(result) {
                    if (result === true) {
                    	controller.removeBook(book);
                    }
                }, "Are you sure?");
    		}
    	});
    	this.bindPropertyIfReserved(btnRemoveTemplate, "enabled", false, true);
    	btnRemoveTemplate.addStyleClass("smallSpaceToRight");
    	setVisibleIfAdmin(btnRemoveTemplate);
    	
    	var btnReturnTemplate = new sap.ui.commons.Button({
    		text : "Return",
    		press : function(event) {
    			
    			this.setEnabled(false);
    			var pathToBook = event.getSource().getBindingContext().getPath();
    			var book = controller.getModel().getProperty(pathToBook);

    			controller.confirmBookReturning(book);
    		}
    	});
    	this.bindPropertyIfReserved(btnReturnTemplate, "enabled", true, false);

    	var actionTemplate = new sap.ui.commons.layout.HorizontalLayout();
    	actionTemplate.addContent(btnReserveTemplate);
    	actionTemplate.addContent(btnEditTemplate);
    	actionTemplate.addContent(btnRemoveTemplate);
    	actionTemplate.addContent(btnReturnTemplate);


    	var tblAllBooks = new sap.ui.table.Table({
    		 columns : [
    		              {
    		            	width : "15%",
    			 			label : "Title",
    		 				template : lnkTitleTemplate,
							sortProperty : "bookName",
							filterProperty : "bookName"
						  },
						  {
							  width : "15%",
			                  label : "Author",
			                  template : "authorName",
			                  sortProperty : "authorName",
			                  filterProperty : "authorName"
			              },
			              {
			            	  width : "10%",
			                  label : "Reserved By",
			                  template : "reservedByUser",
			                  sortProperty : "reservedByUser",
			                  filterProperty : "reservedByUser"
			              },
			              {
			            	  width : "10%",
			                  label : "Reserved Untill",
			                  template : new sap.ui.commons.TextView({
			                      text : {
			                          path : "reservedUntil",
			                          formatter : formatDate
			                      }
			                  }),
			                  sortProperty : "reservedUntil",
			                  filterProperty : "reservedUntil"
			              },
			              {
			            	  width : "15%",
			            	  label : "Overall Rating",
			            	  sortProperty : "bookRating",
			                  template : new sap.ui.commons.RatingIndicator({
			                		maxValue: 5,
			                		editable: false,
			                		visualMode: sap.ui.commons.RatingIndicatorVisualMode.Continuous,
			                		value : {
			                			path : "bookRating"
			                		}
			                	}),
								hAlign : sap.ui.commons.layout.HAlign.Center
			              },
			              {
			            	  width : "35%",
			            	  label : "Action",
			                  template : actionTemplate,
			                  hAlign : sap.ui.commons.layout.HAlign.Center
			              }
    		            ],
    		            selectionMode : sap.ui.table.SelectionMode.None,
    		            visibleRowCount : 10
    	});
    	tblAllBooks.bindRows("/books", undefined, new sap.ui.model.Sorter("bookName", false));

    	matrix.createRow(tblAllBooks);
    	matrix.createRow(btnAdd);
    	matrix.createRow(this.getBookDetailsPanel(controller));

    	return matrix;
    },

    bindPropertyAccordingToISBN : function(element, propertyName, valueIfIsbnExists, valueIfIsbnDoesNotExist) {
    	element.bindProperty(propertyName, "isbn", function(isbn) {

    		if (isbn) {
    			return valueIfIsbnExists;
    		}

    		return valueIfIsbnDoesNotExist;
    	});
    },

    bindPropertyIfReserved : function(element, propertyName, valueIfReserved, valueIfNotReserved) {
    	element.bindProperty(propertyName, "reserved", function(reserved){
			if (reserved) {
				return valueIfReserved;
			}
			return valueIfNotReserved;
		});
    },

    // This function is used for both adding a new book
    // and for editing an existing one
    openDialogBook : function(controller, bookToEdit) {

    	var model = new sap.ui.model.json.JSONModel();

    	if (bookToEdit) {
    		model.setData(bookToEdit);
    		model.setProperty("/previousBookName", bookToEdit.bookName);
    		model.setProperty("/previousAuthorName", bookToEdit.authorName);
    	}

    	// content of the dialog
    	var matrix = new sap.ui.commons.layout.MatrixLayout({
    		width : "90%",
    		columns : 3,
    		widths: ["35%", "55%", "10%"]
    	});
    	matrix.setModel(model);

    	var lblIsbn = new sap.ui.commons.Label({
    		text : "ISBN"
    	});
    	var fldIsbn = new sap.ui.commons.TextField({
    		width : "90%",
    		value : "{/isbn}",
    		change : function(event) {
    			var value = this.getValue();
    			var errorMessage = controller.validateIsbn(value);
    			changeValueState(this, errorMessage);
    			if (errorMessage) {
    				errorMessage = "ISBN: " + errorMessage;
    			}
    			model.setProperty("/isbnErrorMessage", errorMessage);
    			controller.enableSaveButton(model);
    		}
    	});
    	matrix.createRow(lblIsbn, fldIsbn, getInputErrorImage("/isbnErrorMessage"));

    	var lblTitle = new sap.ui.commons.Label({
    		text : "Title"
    	});
    	var fldTitle = new sap.ui.commons.TextField({
    		width : "90%",
    		value : "{/bookName}",
    		change : function(event) {
    			var value = jQuery.trim(this.getValue());
    			var errorMessage = validateNotEmpty(value);
    			changeValueState(this, errorMessage);
    			if (errorMessage) {
    				errorMessage = "Title: " + errorMessage;
    			}
    			model.setProperty("/titleErrorMessage", errorMessage);
    			controller.enableSaveButton(model);
    		}
    	});
    	matrix.createRow(lblTitle, fldTitle, getInputErrorImage("/titleErrorMessage"));

    	var lblAuthor = new sap.ui.commons.Label({
    		text : "Author"
    	});
    	var fldAuthor = new sap.ui.commons.TextField({
    		width : "90%",
    		value : "{/authorName}",
    		change : function(event) {
    			var value = jQuery.trim(this.getValue());
    			var errorMessage = validateNotEmpty(value);
    			changeValueState(this, errorMessage);
    			if (errorMessage) {
    				errorMessage = "Author : " + errorMessage;
    			}
    			model.setProperty("/authorErrorMessage", errorMessage);
    			controller.enableSaveButton(model);
    		}
    	});
    	matrix.createRow(lblAuthor, fldAuthor, getInputErrorImage("/authorErrorMessage"));

    	var btnSave = new sap.ui.commons.Button({
    		text : "Save",
    		press : function(event) {
    			fldIsbn.fireChange();
    			fldTitle.fireChange();
    			fldAuthor.fireChange();

    			if (model.getProperty("/disableSave") === true) {
    				// there is an error in input => return
    				return;
    			}

    			var book = model.getData();
    			controller.saveBook(book);
    			model.setData({});
    		}
    	});
    	btnSave.bindProperty("enabled", "/disableSave", function(bValue){
    		if (bValue === true) {
    			return false;
    		}
    		return true;
    	});

    	matrix.createRow(null, null, btnSave);

    	matrix.createRow(null, null, null); // an empty row for better styling

    	var accordion = new sap.ui.commons.Accordion({
    		width : "100%"
    	});
    	var accordionSection = new sap.ui.commons.AccordionSection({
    		width : "100%",
    		title : "Show predefined books"
    	});
    	var tablePredefinedBooks = this.getPredefinedBooksTable(model);
    	accordionSection.addContent(tablePredefinedBooks);
    	accordion.insertSection(accordionSection);

    	matrix.createRow(matrixColSpan(accordion, 3));

    	var dialog = new sap.ui.commons.Dialog({
    		modal : true,
    		width : "45%"
    	});
    	dialog.setTitle("Create & Edit Books");
    	dialog.addContent(matrix);

    	dialog.open();
    },

    getBookDetailsPanel : function(controller) {

    	var panel = new sap.ui.commons.Panel({
    		title : new sap.ui.commons.Title({
    			text : "Book Details"
    		}),
    		showCollapseIcon : false
    	});
    	panel.bindProperty("visible", "/details/data", function(details){
    		if (details){
    			return true;
    		}
    		return false;
    	});


    	var matrix = new sap.ui.commons.layout.MatrixLayout({
    		width : "100%",
    		columns : 2,
    		widths : ["40%", "60%"]
    	});
    	matrix.addStyleClass("detailsBackground");

    	var image = new sap.ui.commons.Image();
    	image.bindProperty("src", "/details/data/imageLink", function (imageLink) {
    		if (imageLink) {
    			return imageLink;
    		}

    		return "res/img/cover_not_available.png";
    	});
    	image.addStyleClass("smallSpaceToLeft");

    	var detailsTable = new sap.ui.table.Table({
   		 columns : [
   		              {
   		            	width : "40%",
   			 			label : "Key",
   		 				template : "key",
						sortProperty : "key",
						filterProperty : "key"
					  },
					  {
						width : "60%",
			            label : "Value",
			            template : "value"
			           }
   		            ],
   		            selectionMode : sap.ui.table.SelectionMode.None,
   		            visibleRowCount : 7
    	});
    	detailsTable.bindRows("/details/data/pairs");

    	matrix.createRow(matrixRowSpan(image, 2), detailsTable);

    	var linkToBook = new sap.ui.commons.Link({
    		text : "Link to Book",
    		href : "{/details/data/openLibraryUrl}",
    		target : "_blank"
    	});
    	linkToBook.bindProperty("enabled", "/details/data/openLibraryUrl", function(url){
    		if (!url){
    			return false;
    		}
    		return true;
    	});
    	matrix.createRow(linkToBook);


    	panel.addContent(matrix);

    	return panel;
    },

    getPredefinedBooksTable : function(formModel) {

    	var tableModel = new sap.ui.model.json.JSONModel();
    	tableModel.loadData("res/predefined_books/books.json", null, false);

    	var tblPredefinedBooks = new sap.ui.table.Table({
      		 columns : [
      		              {
      		            	width : "40%",
      			 			label : "Title",
      		 				template : "bookName",
      		 				sortProperty : "bookName",
      		 				filterProperty : "bookNAme"
		   				},
		   				{
      		            	width : "40%",
      			 			label : "Author",
      		 				template : "authorName",
      		 				sortProperty : "authorName",
      		 				filterProperty : "authorName"
		   				},
		   				{
		   					width : "20%",
		   					label : "Use",
		   					template : new sap.ui.commons.Button({
		   						text : "Use",
		   						press : function(event) {
		   							var pathToSelectedBook = event.getSource().getBindingContext().getPath();
		   							var selectedBook = tableModel.getProperty(pathToSelectedBook);
		   							formModel.setData(selectedBook);
		   						}
		   					})
		   				}
		      		    ],
      		            selectionMode : sap.ui.table.SelectionMode.None,
      		            visibleRowCount : 4
       	});
    	tblPredefinedBooks.setModel(tableModel);
    	tblPredefinedBooks.bindRows("/");

    	return tblPredefinedBooks;

    }
});
