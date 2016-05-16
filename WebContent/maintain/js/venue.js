maintainApp.controller('VenueListCtrl', getCommonParams(makeListFn("venue", {
	sort : function(venue1, venue2) {
		return venue1.name.localeCompare(venue2.name);
	}
})));

maintainApp.controller('VenueDetailCtrl', getCommonParams(function($scope,
		entityService,  $rootScope, $location) {

	
	
	this.$routerOnActivate = function(next){
		makeUpdateFn("venue")($scope, entityService, next.params, $rootScope,
				$location);
		
	}
	
	$scope.uploadImage = function(){
		
		var file = document.getElementById('venue.image.file').files[0];
		
		entityService.upload(file, "venue/" + $scope.venue.id + "/" + file.name,  function(imageUrl){
			$scope.venue.imageURL = imageUrl;})
	};
}

));
