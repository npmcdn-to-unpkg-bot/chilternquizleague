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
    	scope:{results:"=",
    		type:"="},
    	restrict:'E',
    	templateUrl:'/results/results-table-content.html',
    	link : function(scope, element, attrs){
    		
    		scope.close = function(){$mdDialog.hide()}
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
    		scope.showInfo = function($event,content){
  				
  				$mdDialog.show(
  			      $mdDialog.alert()
  			        .textContent(content)
  			        .ok('Close')
  			        .targetEvent($event))
  			}
    		scope.showReports = function(ev,results, result) {

  				$mdDialog.show({
  					scope : scope,
  					preserveScope: true,
  					templateUrl : '/results/reports.html',
  					controller : "ReportsController",
  					targetEvent : ev,
  					clickOutsideToClose:true,
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

mainApp.directive('cqlFixtures', ["$location",function($location) {
    return {
    	scope:{
    		fixtures:"=",
    		scrollTo:"=",
    		type:"="	
    		},
    	restrict:'E',
    	templateUrl:'/results/fixtures-table-content.html',
    	link : function(scope, element, attrs){
    		scope.rowCount = attrs.rows ? attrs.rows :10000;
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

mainApp.directive('cqlSeasons', ["viewService","seasonService",function(viewService, seasonService) {
    return {
    	scope:{season:"="},
    	restrict:'E',
    	replace:true,
    	link: function(scope, element, attrs){
		
    		if(!scope.season){
       		seasonService.getSeason().then(function(season){
      			scope.season = season;
      		})
    		}
    	
	  		seasonService.getSeasons().then(function(seasons){
	  			scope.seasons = seasons})
    		scope.hidelabel = attrs.hasOwnProperty("hidelabel");
    		scope.selectstyle = attrs.hasOwnProperty("toolstyle") ? {background:"inherit",border:"none",color:"inherit"}:{};
    		scope.containerstyle = attrs.hasOwnProperty("toolstyle") ? {padding:"0",paddingBottom:".45em",paddingLeft:".25em"}:{}
    		scope.toolclass = attrs.hasOwnProperty("toolstyle") ?"tool" : ""
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
		templateUrl : "/common/titlebar.html",
		link: function(scope, element, attrs, ctrl, transclude){
			scope.toggleRight = function(){
				$mdSidenav('right').toggle();
				$mdSidenav('left').close();};
			
			scope.toggleLeft = function(){$mdSidenav('left').toggle();};
			
			scope.pageMenu = attrs.hasOwnProperty("pageMenu");
		}};
}]);

mainApp.directive("cqlPageMenu",function(){
	
	return {
		restrict:'E',
		replace:true,
		template : "<span><md-button class='md-icon-button' ng-click='toggleLeft()' aria-label='Page menu'><md-icon md-svg-src='/images/icons/ic_more_horiz.svg'></md-icon><md-tooltip>Page menu</md-tooltip></md-button></span>"};
});

mainApp.directive("cqlPageNav",["$mdSidenav", function($mdSidenav){

	return {
		scope:{"title":"@"},
		restrict:'E',
		transclude:true,
		link: function(scope){
			scope.closeLeft = function(){
				$mdSidenav('left').close();};
			scope.classes = "md-subheader md-default-theme"
		},
		templateUrl: "/common/sidenav.html"
	}
}])
