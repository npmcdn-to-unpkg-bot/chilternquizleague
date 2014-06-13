var qlApp = angular.module('qlApp', []);

qlApp.controller('ResultsController', [ '$scope','$http',function($scope, $http) {
	$http.get("jaxrs/leaguetable/current",{"responseType":"json"}).success(function(ret){$scope.results = ret;});
		
//		[ {
//		position : 1,
//		team : "Squirrel",
//		played : 1,
//		won : 1,
//		lost : 0,
//		drawn : 0,
//		matchPointsFor : 80,
//		matchPointsAgainst : 77,
//		leaguePoints : 2
//	} ];
} ]);