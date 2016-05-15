mainApp.directive('cqlSeasons', ["viewService","seasonService","$rootScope",function(viewService, seasonService, $rootScope) {
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
    		scope.containerstyle = attrs.hasOwnProperty("toolstyle") ? {padding:"0",paddingBottom:".2em",paddingLeft:".25em"}:{}
    		scope.toolclass = attrs.hasOwnProperty("toolstyle") ?"tool" : ""
    		if(attrs.hasOwnProperty("broadcast")){
    			scope.$watch("season", function(season){$rootScope.$broadcast("season",season)})
    		}
     	},
    	templateUrl:'/directives/seasons/season-dropdown.html'
    	
    };
  }]);