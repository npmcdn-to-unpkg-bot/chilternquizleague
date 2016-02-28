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
	                              			'viewService','seasonService' ,'$location', '$stateParams','$sce',
			listControllerFactory("venue", function($scope, $interval, viewService, seasonService,$location, $stateParams,$sce){
			
				if(!$stateParams.itemId){
					$scope.$watchCollection("venues",function(venues){
						
						if(venues && venues.length > 0){
							
							$scope.$state.go(".detail",{itemId:venues[Math.floor(Math.random() * venues.length)].id});
						}
					});
				}
				
				$scope.$watch("venue", function(venue){
				
					if(venue){
						
						var parts=["https://maps.google.com/maps?&q=","", "&output=embed"];
					
						parts[1] = encodeURIComponent((venue.name + " " +venue.address).replace(/\s/g, "+"));
						
						$scope.embeddedUrl = $sce.trustAsResourceUrl(parts.join());
						$scope.linkUrl = $sce.trustAsResourceUrl(parts.slice(0,2).join());
					}
					
					
				});
				
			}) ]);
	
	mainApp.controller("VenueController", ["$scope", function($scope){
		
		$scope.setCurrentItem();
	}]);
})();