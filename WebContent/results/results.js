(function() {
	
	mainApp.component('results', {
	  templateUrl:"/results/results.html",
	  $routeConfig: [
		 {path: '/all',    name: 'AllResults',   component: 'allResults', useAsDefault: true},
	  ]
	})
	.component('allResults', {
		templateUrl:"/results/results-table.html",
	})
	.component('resultsSidenav', {
		templateUrl:"/results/sidenav.html"
	})
	.component('resultsMenu', {
		templateUrl:"/results/results-menu.html"
	})
	
	

	mainApp.controller("AllResultsController", [
			'$scope',
			'viewService',
			'$location',
			'seasonService',
			function($scope, viewService, $location, seasonService) {
	
				$scope.setSeason = function(season) {

					$scope.allResults = season ? viewService.view("all-results",{id:season.id,isArray:true},function(results){
						if(results){
						results.sort(function(results1,results2){return results2.date -results1.date;});
						}
					}): [];
					
					$scope.season = season;
				};
				
				seasonService.getSeason().then(function(season){$scope.setSeason(season)});
				
				$scope.$on("season", function(evt, season){
					$scope.setSeason(season);
				});


			} ]);
	
	
	mainApp.controller('ResultsTable', [ '$scope', function($scope) {
		
		$scope.$watchCollection("allResults", function(allResults){
			$scope.results = allResults;});
		
	}]);

})();
