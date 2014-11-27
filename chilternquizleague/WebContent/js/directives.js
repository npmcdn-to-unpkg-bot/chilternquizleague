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

mainApp.directive('cqlResults',["$mdDialog", function($mdDialog) {
    return {
    	scope:{results:"="},
    	restrict:'E',
    	templateUrl:'/results/results-table-content.html',
    	link : function(scope, element, attrs){
    		
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
    		scope.showReports = function(ev,results, result) {

  				$mdDialog.show({
  					templateUrl : '/results/reports.html',
  					controller : "ReportsController",
  					targetEvent : ev,
  					locals : {
  						reportsData : {
  							results : results,
  							result : result
  						}
  					}
  				});

  			};
    		
    	}
    	
    };
  }]);

mainApp.directive('cqlFixtures', function() {
    return {
    	scope:{fixtures:"="},
    	restrict:'E',
    	templateUrl:'/results/fixtures-table-content.html',
    	link : function(scope, element, attrs){
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
     	}
    };
  });

mainApp.directive('cqlLeagueTable', function() {
    return {
    	scope:{league:"=leaguetable"},
    	restrict:'E',
    	templateUrl:'/results/league-table-content.html',
    	link : function(scope, element, attrs){
    		scope.clazz = "";
    		scope.clazz = scope.clazz + (attrs.hasOwnProperty("collapse")?"collapse" : "");
    		scope.$watch("league", function(leagueTable){scope.leagueTable = leagueTable;})
     	}
    	
    };
  });

mainApp.directive('cqlSeasons', ["viewService","$rootScope",function(viewService, $rootScope) {
    return {
    	scope:{season:"="},
    	restrict:'E',
    	replace:true,
    	link: function(scope, element, attrs){
    		
    		if(!scope.season){
	    		scope.$watchCollection("seasons", function(seasons){
	    			if(seasons){
	    				for(idx in seasons){
	    					if(seasons[idx].id == $rootScope.global.currentSeasonId){
	    						scope.season = seasons[idx];
	    						break;
	    					}
	    				}
	    			}
	    		});
	    	}
    		
    		scope.seasons = $rootScope.seasons;
    		scope.labelStyle = attrs.hasOwnProperty("hidelabel") ? {display:"none"}:{"margin-right":".25em"};
    		scope.selectStyle = attrs.hasOwnProperty("toolstyle") ? {background:"transparent",border:"none"}:{};
    		

     	},
    	templateUrl:'/common/season-dropdown.html'
    	
    };
  }]);

mainApp.directive("cqlTitleBar",["$mdSidenav", function($mdSidenav){
	
	return {
		scope:{},
		restrict:'E',
		replace:true,
		transclude:true,
		templateUrl : "/common/titleBar.html",
		link: function(scope, element, attrs, ctrl, transclude){
			scope.toggleRight = function(){
				$mdSidenav('right').toggle();
				$mdSidenav('left').close();}
			
			scope.toggleLeft = function(){$mdSidenav('left').toggle()};
			
			scope.pageMenu = attrs.hasOwnProperty("pageMenu");
		}};
}]);

mainApp.directive("cqlPageMenu",function(){
	
	return {
		restrict:'E',
		replace:true,
		template : "<span hide-tablet-landscape hide-pc><md-button ng-click='toggleLeft()' aria-label='Page menu'><md-icon icon='/images/icons/ic_more_horiz.svg'></md-icon><md-tooltip>Page menu</md-tooltip></md-button></span>"};
});