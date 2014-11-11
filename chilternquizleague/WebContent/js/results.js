(function() {
	

	mainApp.config([ '$stateProvider',
		function($stateProvider) {
			$stateProvider
			.state('results', {
				templateUrl : '/results/results.html'
			})
			.state('results.all', {
				url : "/results/all",
				templateUrl : '/results/results-table.html'
			})
			.state('results.submit', {
				url : "/results/submit",
				templateUrl : '/results/submit-results.html'
			});
		} ]);	

	function nowOrBefore(input) {
		function makeDateString(date) {
			return new Date(date).toISOString();
		}

		var now = makeDateString(new Date());
		var ret = [];
		for (idx in input) {
			if (makeDateString(input[idx].start) <= now) {
				ret.push(input[idx]);
			}
		}

		return ret.sort(function(item1, item2) {
			return item2.start - item1.start;
		});
	}

	mainApp.controller('ResultsSubmitController', [ '$scope', 'viewService',
			'$location', function($scope, viewService, $location) {

				$scope.fixturesForEmail = function(email) {

					if(!email) return;
					
					$scope.fixture = null;
					
					viewService.view("fixtures-for-email", {
						email : email,
						seasonId : $scope.global.currentSeasonId,
						isArray : true
					}, function(preSubmission) {

						if(preSubmission.fixtures)
						{
							var fixtures = nowOrBefore(preSubmission.fixtures);
													
							$scope.fixture = null;
	
							//loop once only
							for (idx in fixtures) {
	
								var fixture = fixtures[idx].fixtures.pop();
								$scope.fixtures = fixtures[idx];
								$scope.fixture = fixture;
								$scope.mainResult = {
									fixture : $scope.fixture,
									reports : [ {
										team:preSubmission.team,
										text:{text : ""}
									} ]
								};
								$scope.beerResult = {
									fixture : $scope.fixture
								};
								break;
							}
						}


					});
				};
				
				$scope.$watch("email",$scope.fixturesForEmail);

				$scope.submitResults = function() {

					viewService.post("submit-results", [ {
						email: $scope.email,
						result : $scope.mainResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType :  $scope.fixtures.competitionType

					}, {
						email: $scope.email,
						result : $scope.beerResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType : "BEER"

					} ]);

					$scope.fixture = null;
				};
			} ]);

	mainApp.controller("AllResultsController", [
			'$scope',
			'viewService',
			'$location',
			function($scope, viewService, $location) {

				$scope.setCurrentResults = function(results) {
					$scope.currentResults = results;
				};

				$scope.setSeason = function(season) {

					$scope.allResults = season ? viewService.view("all-results",{id:season.id,isArray:true},function(results){
						if(results){
						results.sort(function(results1,results2){return results2.date -results1.date;});
						}
					}): [];
					
					$scope.season = season;
					
					$scope.$watchCollection("allResults",function(allResults){
						$scope.setCurrentResults(allResults && allResults.length > 0 ? allResults[0] : null);});

				};

				viewService.view("season-views",{isArray:true}, function(seasons) {
					$scope.seasons = seasons;

					for (idx in seasons) {

						if (seasons[idx].id == $scope.global.currentSeasonId) {
							$scope.setSeason(seasons[idx]);
							return;
						}

						$scope.setSeason(seasons ? seasons[0] : null);
					}

				});

			} ]);
	
	mainApp.controller('ResultsTable', [ '$scope', function($scope) {
		
		$scope.$watchCollection("allResults", function(allResults){
			$scope.results = allResults;});
		
		$scope.showReports = function(results, result){
			
			$scope.reportsData = {results:results,result:result};
			$scope.popupclass="popup";
			
		};
		
		$scope.closeWindow = function() {
			$scope.popupclass = "popdown";
			$scope.reports = null;
		};
	}]);
	

	mainApp.controller("ReportsController", [ '$scope','viewService',
	function($scope,viewService) {

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
