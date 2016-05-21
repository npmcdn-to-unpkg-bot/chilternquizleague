maintainApp.controller('VenueListCtrl', getCommonParams(function($scope, ctrlUtil){
	ctrlUtil.makeListFn("venue", $scope)
}));

maintainApp.controller('VenueDetailCtrl', getCommonParams(function($scope,
		ctrlUtils) {

	ctrlUtils.makeUpdateFn("venue",$scope, this);
	
	$scope.uploadImage = function(){
		
		var file = document.getElementById('venue.image.file').files[0];
		
		entityService.upload(file, "venue/" + $scope.venue.id + "/" + file.name,  function(imageUrl){
			$scope.venue.imageURL = imageUrl;})
	};
}

));
