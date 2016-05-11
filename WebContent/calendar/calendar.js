(function() {

	mainApp.component('calendar', {
	  templateUrl:"/calendar/calendar.html",
	  controller : "CalendarController"
	})
	
	

	mainApp.controller("CalendarController", ["$scope","viewService","seasonService" , "$sce", "$rootScope",
			function($scope,viewService,seasonService, $sce, $rootScope ){
		
		seasonService.getSeason().then(function(season){$scope.season = season});
		$scope.fixtures = {}
		$scope.results = {}
		
		$scope.$on("season", function(evt, season){
			if(season) $scope.calendar = viewService.view("calendar",{seasonId:season.id})
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
		
		$scope.loadResults = function(id){
			
			viewService.viewP("resultsById",{id:id}).then(function(results){
				$scope.results["f" + id] = [results]
			})
		}
		
		$scope.getResults = function(id){
			return $scope.results["f" + id]
		}

		
		
		
	}]);
	
})()
		