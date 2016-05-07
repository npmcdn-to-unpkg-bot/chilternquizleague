(function() {

	function sortVenues(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
	;

	mainApp.config([ '$stateProvider', function($stateProvider) {
		$stateProvider.state("venues", {
			url : "/venues",
			templateUrl : '/venue/venues.html'
		}).state("venues.detail",{
			url : "/:itemId",
			templateUrl : '/venue/venue-detail.html',
			controller : "VenueDetailController"
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
    				
    			}) ]);
	mainApp.controller("VenueDetailController", ["$scope", function($scope){
		
		$scope.setCurrentItem();
	}]);
	
	
	mainApp.controller('VenueController', [ '$sce',	function ($sce){
		
		function makeParts(venue){
			var parts=["https://maps.google.com/maps?&q=","", "&output=embed"];
			
			parts[1] = encodeURIComponent((venue.name + " " +venue.address).replace(/\s/g, "+"));
			return parts
		}		
		
		this.linkUrl = function(venue){
					return $sce.trustAsResourceUrl(makeParts(venue).slice(0,2).join());
				}
				
		this.embeddedUrl = function(venue){
					return $sce.trustAsResourceUrl(makeParts(venue).join());
				}


	}])

	mainApp.component('venue', {
	  templateUrl: '/venue/venue-template.html',
	  controller: "VenueController",
	  bindings: {
	    venue: '='
	  }
	});
})();