(function() {
	
	mainApp.config([ '$stateProvider', function($stateProvider) {
		
		$stateProvider
		.state("teams", {
			
			url:"/teams",
			templateUrl : '/team/teams.html'
			
		})
		.state("teams.all", {
			
			url:"/all",
			templateUrl : '/team/teams-header.html'
			
		})
		.state("teams.team",{
			
			url:"/team/:itemId",
			templateUrl : '/team/team.html'
		})
		.state("teams.results",{
			
			url:"/team/:itemId/results",
			templateUrl : '/team/team-results.html'
		})
		.state("teams.fixtures",{
			
			url:"/team/:itemId/fixtures",
			templateUrl : '/team/team-fixtures.html'
		})
		.state("teams.start",{
			
			url:"/start-team",
			templateUrl:'/team/start-team.html'
		});
		

	}]);

	function extraStuff($scope, $interval, viewService, $location,$stateParams) {
		
		$scope.showContact = function(){
	
		};
		
	
		$scope.makeICal = function(team) {

			var contents = generateICalContent(team.extras.fixtures);

			var filename = team.shortName.replace(/\s/g, "_") + "_fixtures"
					+ ".ics";

			var blob = new Blob([ contents ], {
				type : "text/calendar;charset=utf-8"
			});

			saveAs(blob, filename);

		};
	

		$scope.$watch("global.currentSeasonId", function(currentSeasonId) {

			if (currentSeasonId) {
				
				
				$scope.$watch("team", function(team) {

					if (team) {
						if (!team.extras) {
							team.extras = viewService.view("team-extras", {
								seasonId : currentSeasonId,
								teamId : team.id
							});
						}
					}
				});

			}
		});
	}

	mainApp.controller('FindTeams',['$scope', 'viewService', function($scope,viewService){

		

	}]);
	


	mainApp.controller('TeamsController', [ '$scope', '$interval', 'viewService',
			'$location', '$stateParams',			cyclingListControllerFactory("team", function(team1, team2) {
				return team1.shortName.localeCompare(team2.shortName);
			}, extraStuff) ]);

	
	mainApp.controller("TeamExtrasController", [ '$scope','$interval' ,'viewService',
		'$location',  function($scope, $interval,viewService, $location) {
			
			$scope.setSeason = function(season){$scope.season = season;};

			var loadSeasons = listAndSelection("season", $scope, viewService,{remoteListName:"season-views"});
			
			function teamExtras(){
				
				if($scope.team && $scope.season && !$scope.team.extras){
					viewService.view("team-extras", {
						seasonId : $scope.season.id,
						teamId : $scope.team.id
					}, function(extras){
						if($scope.team.id != extras.id){
						$scope.team.extras = extras;
						$scope.results = extras.results;}});

				}
			}
			
			loadSeasons($scope.global.currentSeasonId);
			
			$scope.$watch("global.currentSeasonId", loadSeasons);
			$scope.$watch("season", teamExtras);
			$scope.$watch("team.id", teamExtras);
			

			
		} ]);
	


	mainApp.controller("TeamFixturesTable", [ '$scope', function($scope) {

		function loadResults(fixtures) {

			$scope.$watchCollection("team.extras.fixtures", function(fixtures) {
				$scope.fixtures = fixtures;
			});

			$scope.fixtures = fixtures;

		}

		loadResults($scope.team && $scope.team.extras ? $scope.team.extras.fixtures:null);

	} ]);

	mainApp.controller("TeamResultsTable", [ '$scope', function($scope) {

		function loadResults(results) {

			$scope.$watchCollection("team.extras.results", function(results) {
				$scope.results = results;
			});

			$scope.results = results;

		}
		
		loadResults($scope.team && $scope.team.extras ? $scope.team.extras.results : null);
		
		$scope.showReports = function(results, result){
			
			$scope.reportsData = {results:results,result:result};
			$scope.popupclass="popup";
			
		};
		
		$scope.closeWindow = function() {
			$scope.popupclass = "popdown";
			$scope.reports = null;
		};
	
	}]);

			mainApp.controller("ReportsController", [ '$scope', '$interval',
			'viewService', '$location',
			function($scope, $interval, viewService, $location) {

				$scope.$watch("reportsData", function(reportsData) {
					if (reportsData) {
						$scope.reports = viewService.view("reports", {
							resultsKey : reportsData.results.key,
							homeTeamId : reportsData.result.fixture.home.id
						});
					}
				});



			} ]);



})();
