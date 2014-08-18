(function() {

	function sortVenues(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
	;

	mainApp.config([ '$routeProvider', '$locationProvider',
			function($routeProvider, $locationProvider) {
				$routeProvider.when('/venues/:itemId?/:template?', {
					templateUrl : '/venue/venues.html'
				});
			} ]);

	mainApp.controller('VenuesController', [ '$scope', '$interval',
			'viewService', '$location','$routeParams','$sce',
			cyclingListControllerFactory("venue", sortVenues, function($scope,$interval,
					viewService, $location,$routeParams,$sce){
				
				$scope.$watch("venue", function(venue){
				
					if(venue){
						
						var parts=["https://www.google.com/maps/embed/v1/place?q=","", "&key=AIzaSyA8kxsrD-WbEklq5L2jr_mquEftsV9Gsgc"];
					
						parts[1] = venue.address.replace(/\s/g, "+");
						
						$scope.searchAddress = $sce.trustAsUrl(parts.join(parts));
					}
					
					
				});
				
			}) ]);
})();