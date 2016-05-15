mainApp.directive('cqlLeagueTable', function() {
    return {
    	scope:{league:"=leaguetable"},
    	restrict:'E',
    	templateUrl:'/directives/leagueTable/league-table-content.html',
    	link : function(scope, element, attrs){
    		scope.clazz = "";
    		scope.clazz = scope.clazz + (attrs.hasOwnProperty("collapse")?"collapse" : "");
    		scope.$watch("league", function(leagueTable){scope.leagueTable = leagueTable;})
     	}
    	
    };
  });
