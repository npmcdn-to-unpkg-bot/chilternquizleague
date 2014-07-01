var teamApp = angular.module('teamApp', [ "ngRoute" ]).factory('viewService',
		VIEW_SERVICE_DEFN);

teamApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/team/:teamId/:cycle', {
		templateUrl : '/team/team.html',
		controller : 'TeamController'
	}).when('/team/:teamId', {
		templateUrl : '/team/team.html',
		controller : 'TeamController'
	}).otherwise({
		redirectTo : ''
	});
} ]);

var promise;

teamApp.controller('TeamsController', [
		'$scope',
		'$http',
		'$interval',
		'viewService',
		'$location',
		function($scope, $http, $interval, viewService, $location) {

			viewService.load("globaldata", function(globalData) {

				$scope.leagueName = globalData.leagueName;
			});

			viewService.load("teams", function(teams) {
				$scope.teams = teams;



				var teamIndex = -1;
				promise = $interval(function() {
					$location.url("/team/"
							+ $scope.teams[teamIndex = teamIndex >= (teams.length -1) ? 0 : (teamIndex + 1)].id + "/true");
				}, 5000);

			});

		} ]);

teamApp.controller('TeamController',
		[
				'$scope',
				'$http',
				'$interval',
				'$routeParams',
				'viewService',
				'$location',
				function($scope, $http, $interval, $routeParams, viewService,
						$location) {

					!$routeParams.cycle && promise ? $interval.cancel(promise) : null;
					
					var teamId = $routeParams.teamId;

					viewService.load("team", function(team) {
						$scope.team = team;
					}, {
						id : teamId
					});

					viewService.load("globaldata", function(globalData) {

						$scope.global = globalData;

						viewService.load("team-fixtures", function(fixtures) {

							for (idx in fixtures) {

								fixtures[idx].date = new Date(
										fixtures[idx].date);
							}

							$scope.fixtures = fixtures;
						}, {
							seasonId : globalData.currentSeasonId,
							teamId : teamId
						});
					});

				} ]);