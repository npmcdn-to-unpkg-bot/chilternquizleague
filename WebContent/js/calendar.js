(function() {

	mainApp.config([ "$stateProvider", function($stateProvider) {
		$stateProvider.state("calendar", {
			url : "/calendar_view",
			templateUrl : "/calendar/calendar.html"
		})
	} ]);

	mainApp.controller("CalendarController", ["$scope","viewService","seasonService" , "$sce",
			function($scope,viewService,seasonService, $sce ){
		
		$scope.fixtures = {}
		
		seasonService.getSeason().then(function(season){
			$scope.calendar = viewService.view("calendar",{seasonId:season.id})
		})
		
		$scope.embeddedMap = function(venue){
			
			var parts=["https://maps.google.com/maps?&q=","", "&output=embed"];
			
			parts[1] = encodeURIComponent((venue.name + " " +venue.address).replace(/\s/g, "+"));
			
			return $sce.trustAsResourceUrl(parts.join());

			
		}
		
		$scope.loadFixtures = function(id){
			
			viewService.viewP("fixturesById",{id:id}).then(function(fixtures){
				$scope.fixtures["f" + id] = [fixtures]
			})
		}
		
		$scope.getFixtures = function(id){
			return $scope.fixtures["f" + id]
		}
		
		
		
	}]);
	
})()
		