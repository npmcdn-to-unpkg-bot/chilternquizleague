(function() {

	function sortVenues(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
	;

	mainApp.config([ '$stateProvider', function($stateProvider) {
		$stateProvider.state("venues", {
			url : "/venues",
			templateUrl : '/venue/venues.html'
		}).state("venues.detail", {
			url : "/:itemId",
			templateUrl : '/venue/venue-detail.html'
		});
	} ]);

	mainApp.controller('VenuesController', [  '$scope', '$interval',
	                              			'viewService', '$location', '$stateParams','$sce',
			cyclingListControllerFactory("venue", function($scope, $interval, viewService, $location, $stateParams,$sce){
				
//				$scope.$watchCollection("venues",function(venues){
//					
//					if(venues && venues.length > 0){
//						
//						$location.url("/venues/" + venues[Math.floor(Math.random() * venues.length)].id);
//					}
//				});
				
				$scope.$watch("venue", function(venue){
				
					if(venue){
						
						var parts=["https://www.google.com/maps/embed/v1/place?q=","", "&key=AIzaSyA8kxsrD-WbEklq5L2jr_mquEftsV9Gsgc"];
					
						parts[1] = venue.address.replace(/\s/g, "+");
						
						$scope.searchAddress = $sce.trustAsUrl(parts.join(parts));
					}
					
					
				});
				
			}) ]);
	
	mainApp.controller("VenueController", ["$scope", function($scope){
		
		$scope.setCurrentItem();
	}]);
})();