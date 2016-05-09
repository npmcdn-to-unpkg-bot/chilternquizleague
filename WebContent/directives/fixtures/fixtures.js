mainApp.directive('cqlFixtures', ["$location",function($location) {
    return {
    	scope:{
    		fixtures:"=",
    		scrollTo:"=",
    		type:"="	
    		},
    	restrict:'E',
    	templateUrl:'/directives/fixtures/fixtures-table-content.html',
    	link : function(scope, element, attrs){
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
       		scope.cardStyle = attrs.hasOwnProperty("noCard") ? {'box-shadow':'unset'} : {};
    		scope.afterNow = function(){
        		var now = new Date().getTime();
        		var showAll = attrs.hasOwnProperty("showAll");
     			return function(fixtures){
    				return showAll || fixtures.start > now;
    			};
    		};
    		scope.$evalAsync(function(){
    			if(scope.scrollTo){
					var now = new Date().getTime()
					var fixtures = scope.fixtures
					fixtures = fixtures ? fixtures.sort(function(fixtures1,fixtures2){return fixtures1.start -fixtures2.start;}) : fixtures
					for(idx in fixtures){
						if(fixtures[idx].start > now){
							$location.hash("f" +fixtures[idx].start)
							break;
						}
					}}}) 
     	}
    };
  }]);
