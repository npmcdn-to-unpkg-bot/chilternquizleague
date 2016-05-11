(function() {
	
	mainApp.component('reports', {
	  templateUrl:"/reports/reports.html",
	  $routeConfig: [
		 {path: '/all',    name: 'AllReports',   component: 'allReports', useAsDefault: false},
	  ]
	})
	.component('allReports', {
		templateUrl:"/reports/all-reports.html",
	})
	.component('reportsMenu', {
		templateUrl:"/reports/all-reports-menu.html"
	})
	


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
