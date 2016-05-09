mainApp.directive('cqlText', ['htmlifyFilter','viewService',function(htmlify,viewService) {
    return {
     restrict: 'A',
     scope:{},
 
      link: function(scope, element, attrs){
    	  
    	  scope.$watch(attrs.cqlText, function(name){
    		  if(name){
        		  scope.text = viewService.text(name); 
    		  }

    	  });
    	  
    	  scope.$watch("text.text", function(text){
    		  
    		  if(text){
    			  element.html(htmlify(text.replace(/\\\"/g,"\"").replace(/\\n/g,"").replace(/^\"/,"").replace(/\"$/,"")));
    		  }
    		  
    	  });
    	  
    	 
      }
    };
  }]);