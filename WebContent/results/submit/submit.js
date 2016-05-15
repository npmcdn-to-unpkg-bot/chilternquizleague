(function() {
	
	mainApp.component('submitResults', {
	  templateUrl:"/results/submit/submit.html",
	})
	.component('submitResultsForm', {
		templateUrl:"/results/submit/submit-results.html",
	})
	.component('submitResultsMenu', {
		templateUrl:"/results/submit/submit-menu.html"
	})
	
	

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
						templateUrl:'/results/submit/results-confirm-dialog.html',
						clickOutsideToClose:false,
						controller: ['$scope', function($scope) { 
							    $scope.submissions = submissions;
							    $scope.commit = commit;
							    $scope.closeDialog = $mdDialog.hide;
							    
							  }]
					});
					
					

				};
			} ]);

})();
