(function() {

	mainApp.config([ '$stateProvider', function($stateProvider) {

		$stateProvider.state("teams", {

			url : "/teams",
			templateUrl : '/team/teams.html'

		}).state("teams.all", {

			url : "/all",
			templateUrl : '/team/teams-header.html'

		}).state("teams.team", {

			url : "/team",
			templateUrl : '/team/team.html'
		})

		.state("teams.team.id", {

			url : "/:itemId",
			templateUrl : '/team/team-details.html'
		}).state("teams.team.results", {

			url : "/:itemId/results",
			templateUrl : '/team/team-results.html'
		}).state("teams.team.fixtures", {

			url : "/:itemId/fixtures",
			templateUrl : '/team/team-fixtures.html'
		}).state("teams.start", {

			url : "/start-team",
			templateUrl : '/team/start-team.html'
		});

	} ]);

	function extraStuff($scope, $interval, viewService, $location, $stateParams) {


		$scope.makeICal = function(team) {

			var contents = generateICalContent(team.extras.fixtures);

			var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
					+ ".ics";

			var blob = new Blob([ contents ], {
				type : "text/calendar;charset=utf-8"
			});

			saveAs(blob, filename);

		};

		var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
		
		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {
			if (currentSeasonId) {
				
				loadSeasons(currentSeasonId);
			}
		});
	}

	mainApp.controller('FindTeams', [ '$scope', 'viewService',
			function($scope, viewService) {

			} ]);

	mainApp.controller('TeamsController', [ '$scope', '$interval',
			'viewService', '$location', '$stateParams',
			 listControllerFactory("team", extraStuff)]);

	mainApp.controller('TeamController', [ '$scope', 
			function($scope) {

	} ]);

	mainApp.controller("TeamExtrasController", [
			'$scope',
			'$interval',
			'viewService',
			'$location',
			function($scope, $interval, viewService, $location) {

				$scope.setCurrentItem();
				
				function teamExtras() {

					if ($scope.team && $scope.season ) {
						viewService.view("team-extras", {
							seasonId : $scope.season.id,
							teamId : $scope.team.id
						}, function(extras) {
							if ($scope.team.id == extras.id) {
								$scope.team.extras = extras;
							}
						});

					}
				}

				$scope.$watch("season", teamExtras);
				$scope.$watch("team", teamExtras);

			} ]);

})();
