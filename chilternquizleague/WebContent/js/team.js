var teamApp = angular.module('teamApp', [ "ngRoute" ]).factory('viewService',
		VIEW_SERVICE_DEFN);

teamApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/team/:teamId', {
		templateUrl : '/team/team.html',
		controller : 'TeamsController'
	}).otherwise({
		redirectTo : ''
	});
} ]);
teamApp.controller('TeamsController', [ '$scope', '$http', '$interval',
		'viewService', function($scope, $http, $interval, viewService) {

			viewService.load("globaldata", function(globalData) {

				$scope.leagueName = globalData.leagueName;
			});

			viewService.load("teams", function(teams) {
				$scope.teams = teams;
			});

		} ]);