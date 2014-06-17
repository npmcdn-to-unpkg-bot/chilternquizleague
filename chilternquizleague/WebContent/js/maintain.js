var maintainApp = angular.module('maintainApp', [ 'ngRoute' ]);

maintainApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/venues', {
		templateUrl : 'venue/venue-list.html',
		controller : 'VenueListCtrl'
	}).when('/venues/:venueId', {
		templateUrl : 'venue/venue-detail.html',
		controller : 'VenueDetailCtrl'
	}).when('/teams', {
		templateUrl : 'team/team-list.html',
		controller : 'TeamListCtrl'
	}).when('/teams/:teamId', {
		templateUrl : 'team/team-detail.html',
		controller : 'TeamDetailCtrl'
	}).otherwise({
		redirectTo : ''
	});
} ]);

function makeUpdateFn(typeName) {

	return function($scope, $http) {

		$scope.master = {};

		$http.get(
				"jaxrs/" + typeName + "/"
						+ document.location.hash.split("/").pop(), {
					"responseType" : "json"
				}).success(function(ret) {
			$scope.master = ret;

			$scope.reset();
		});

		$scope.update = function(entity) {

			$scope.master = angular.copy(entity);
			$http.post("jaxrs/" + typeName, $scope.master);
			document.location = "#" + typeName + "s";
		};

		$scope.reset = function() {
			$scope[typeName] = angular.copy($scope.master);
		};

	};

}

function makeListFn(typeName) {

	return function($scope, $http) {

		$http.get("jaxrs/" + typeName + "-list", {
			"responseType" : "json"
		}).success(function(ret) {
			$scope[typeName + "s"] = ret;
		});
	};
}

maintainApp.controller('VenueListCtrl', [ '$scope', '$http',
		makeListFn("venue") ]);

maintainApp.controller('VenueDetailCtrl', [ '$scope', '$http',
		makeUpdateFn("venue") ]);

maintainApp.controller('TeamListCtrl',
		[ '$scope', '$http', makeListFn("team") ]);

maintainApp.controller('TeamDetailCtrl', [ '$scope', '$http',
		function($scope, $http) {
			makeUpdateFn("team")($scope, $http);
			makeListFn("venue")($scope, $http);
			
		} ]);