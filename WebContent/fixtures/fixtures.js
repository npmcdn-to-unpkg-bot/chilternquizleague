(function() {
	
	mainApp.component('fixtures', {
	  templateUrl:"/fixtures/fixtures.html",
	  $routeConfig: [
		 {path: '/all',    name: 'AllFixtures',   component: 'allFixtures', useAsDefault: true},
	  ]
	})
	.component('allFixtures', {
		templateUrl:"/fixtures/fixtures-table.html",
		controller : "AllFixturesController"
	})
	.component('fixturesMenu', {
		templateUrl:"/fixtures/fixtures-menu.html"
	})
	
	
	
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
	
})();
	
