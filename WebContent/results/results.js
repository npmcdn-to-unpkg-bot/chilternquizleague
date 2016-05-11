(function() {
	

	mainApp.config([ '$stateProvider',
		function($stateProvider) {
			$stateProvider
			.state('results', {
				templateUrl : '/results/results.html'
			})
			.state('results.all', {
				url : "/results/all",
				views:{
					menu:{templateUrl:"/results/results-menu.html"},
					content:{templateUrl:"/results/results-table.html"}
				}

			})
			.state('results.fixtures', {
				url : "/fixtures/all",
				views:{
					menu:{templateUrl:"/results/fixtures-menu.html"},
					content:{templateUrl:"/results/fixtures-table.html"}
				}
			})
			.state('results.reports', {
				url : "/reports/all",
				views:{
					menu:{templateUrl:"/results/all-reports-menu.html"},
					content:{templateUrl:"/results/all-reports.html"}
				}

			})
			.state('results.submit', {
				url : "/results/submit",
				views:{
					menu:{template:"<cql-title-bar page-menu md-theme='red'>Submit Results</cql-title-bar>"},
					content:{templateUrl:"/results/submit-results.html"}
				}
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



	
	mainApp.controller('ResultsSubmitController', [ '$scope', 'viewService','seasonService',
			'$mdDialog', function($scope, viewService, seasonService,$mdDialog) {

				$scope.fixturesForEmail = function(email) {

					if(!email) return;
					
					$scope.preSubmission = null
					
					seasonService.getGlobal().then(function(global){					
						$scope.global = global
						viewService.view("results-for-submission", {
						email : email,
						seasonId : global.currentSeasonId,
						isArray : false}, function(presub){
							
							$scope.results = angular.copy(presub.results).map(function(result){
								result.result.homeScore = result.result.homeScore > 0 ? result.result.homeScore : null
								result.result.awayScore = result.result.awayScore > 0 ? result.result.awayScore : null
								return result		
							})
							$scope.preSubmission = presub
							
						}						
					);})
					

				};
				
				$scope.$watch("email",$scope.fixturesForEmail);
				$scope.isInvalid = function(){
					
					var retval =($scope.results.reduce(function(acc, result){
						return acc * result.result.homeScore * result.result.awayScore
					},1))
					return retval <= 0 

					
				}

				$scope.submitResults = function() {

					$scope.committed = false
					var results = $scope.results;
					var submissions =  results.map(function(result){
						return {
							email: $scope.email,
							result : result.result,
							seasonId : $scope.global.currentSeasonId,
							competitionType :  result.compType.name,
							description: result.compType.description
						}
					})
					
					function commit() {
						
						if(!$scope.committed){
							viewService.post("submit-results", submissions);
							$scope.committed = true;
						}
						$mdDialog.hide();

						$scope.preSubmission = null;

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
	
				$scope.setSeason = function(season) {

					$scope.allResults = season ? viewService.view("all-results",{id:season.id,isArray:true},function(results){
						if(results){
						results.sort(function(results1,results2){return results2.date -results1.date;});
						}
					}): [];
					
					$scope.season = season;
				};
				
				$scope.$on("season", function(evt, season){
					$scope.setSeason(season);
				});


			} ]);
	
	mainApp.controller("AllFixturesController", [
		'$scope',
		'viewService',
		'$location',
		function($scope, viewService, $location) {
			
			$scope.setSeason = function(season) {
	
				$scope.allFixtures = season ? viewService.view("all-fixtures",{id:season.id,isArray:true}): [];
				
				$scope.season = season;
			};
			
			$scope.$on("season", function(evt, season){
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
	mainApp.controller("AllReportsFatController", ['$scope', function($scope){
		
		$scope.selectedDate = {}
		$scope.setDate = function(date){$scope.selectedDate = (date == $scope.selectedDate ? {} : date)}
		
	}]);
	
	mainApp.controller("AllReportsController", [ '$scope','viewService',
      	function($scope,viewService) {
		
			$scope.setReportsData = function(reportsData){	
      		
      		if (reportsData) {
      				$scope.reports = viewService.view("reports", {
      					resultsKey : reportsData.results.key,
      					homeTeamId : reportsData.result.fixture.home.id

      			});}
      		};

      	} ]);


})();
