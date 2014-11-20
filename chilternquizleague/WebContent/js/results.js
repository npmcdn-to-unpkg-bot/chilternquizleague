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
			'$mdDialog', function($scope, viewService, $mdDialog) {

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

					var submissions =  [ {
						email: $scope.email,
						result : $scope.mainResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType :  $scope.fixtures.competitionType

					}, {
						email: $scope.email,
						result : $scope.beerResult,
						seasonId : $scope.global.currentSeasonId,
						competitionType : "BEER"

					} ];
					

				 function commit() {
						viewService.post("submit-results", submissions);
						$mdDialog.hide();

						$scope.fixture = null;
					}
					
					$mdDialog.show({
						templateUrl:'/results/results-confirm-dialog.html',
						clickOutsideToClose:false,
						controller: ['$scope', function($scope) { 
							    $scope.submissions = submissions;
							    $scope.commit = commit;
							    $scope.closeDialog = $mdDialog.hide;
							    
							  }]
					});
					
					

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
				
				$scope.$watch("season", function(season){
					$scope.setSeason(season);
				});


			} ]);
	
	mainApp.controller('ResultsTable', [ '$scope', function($scope) {
		
		$scope.$watchCollection("allResults", function(allResults){
			$scope.results = allResults;});
		
	}]);
	

	mainApp.controller("ReportsController", [ '$scope','viewService','reportsData',
	function($scope,viewService,reportsData) {

		$scope.reportsData = reportsData;	
		
		if (reportsData) {
				$scope.reports = viewService.view("reports", {
					resultsKey : reportsData.results.key,
					homeTeamId : reportsData.result.fixture.home.id

			});}

	} ]);


})();
