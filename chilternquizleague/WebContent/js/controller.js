var qlApp = angular.module('qlApp', [ "ngRoute" ]).factory('entityService',
		ENTITY_SERVICE_DEFN);

qlApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/team/:teamId', {
		templateUrl : 'team/teams.html',
		controller : 'GlobalDetailCtrl'
	}).otherwise({
		redirectTo : ''
	});
} ]);

qlApp.controller('TeamsController', [
		'$scope',
		'$http',
		'$interval',
		function($scope, $http, $interval) {

			$http.get("jaxrs/globaldata", {
				"responseType" : "json"
			}).success(
					function(globalData) {

						$scope.leagueName = globalData.leagueName;
						$scope.frontPageText = globalData.frontPageText;

						var promise = $interval(function() {

							$http.get(
									"jaxrs/leaguetable/"
											+ globalData.currentSeasonId, {
										"responseType" : "json"
									}).success(function(ret) {
								$scope.season = ret;
							}).error(function() {
								$interval.cancel(promise)
							});
						}, 1000, 120);
					});

		} ]);

qlApp.controller('TeamsController', [ '$scope', '$http', '$interval',
		'entityService', function($scope, $http, $interval, entityService) {

			entityService.getList("team", function(teams) {
				$scope.teams = teams;
			});

		} ]);