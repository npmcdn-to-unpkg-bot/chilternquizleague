(function(){
var teamApp = angular.module('teamApp', [ "ngRoute" ]).factory('viewService',
		VIEW_SERVICE_DEFN);

teamApp.filter('afterNow', function() {
    return function(input) {
     var now = new Date().toDateString();
     var ret = [];
     for(idx in input){
    	 if(input[idx].date.toDateString() >= now){
    		 ret.push(input[idx]);
    	 }
     }
     
     return ret;
    };
  });

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

			viewService.view("globaldata", function(globalData) {

				$scope.leagueName = globalData.leagueName;
			});

			viewService.list("teams", function(teams) {
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

					viewService.load("team", teamId, function(team) {
						$scope.team = team;
					});

					viewService.view("globaldata", function(globalData) {

						$scope.global = globalData;

						viewService.view("team-fixtures",{
							seasonId : globalData.currentSeasonId,
							teamId : teamId
						}, function(fixtures) {

							for (idx in fixtures) {

								fixtures[idx].date = new Date(
										fixtures[idx].date);
							}

							$scope.fixtures = fixtures;
						} );
					});

				} ]);
})();